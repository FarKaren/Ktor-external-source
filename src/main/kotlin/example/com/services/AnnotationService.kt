package example.com.services

import example.com.commands.SearchAnnotationCommand
import example.com.exceptions.AnnotationNotFoundException
import example.com.model.Annotation
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.concurrent.Executors

class AnnotationService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(AnnotationService::class.java)
        private val DATABASE_PATH: String = System.getenv("DATABASE_PATH")
            ?: throw IllegalStateException("DATABASE_PATH environment variable not set")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun findAnnotation(command: SearchAnnotationCommand): Annotation? =
        withContext(Dispatchers.IO) {
            log.info("findAnnotation() method invoke")
            val chunkSize = 64 * 1024 * 1024 // 64 MB chunks
            val path = Paths.get(DATABASE_PATH)
            val fileSize = Files.size(path)
            val numberOfChunks = (fileSize + chunkSize - 1) / chunkSize
            val found = CompletableDeferred<Annotation?>()

            val result = withTimeoutOrNull(5000L) {
                coroutineScope {
                    val jobs = mutableListOf<Job>()
                    val pool =
                        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).asCoroutineDispatcher()

                    try {
                        for (i in 0 until numberOfChunks) {
                            val start = i * chunkSize
                            val end = minOf((i + 1) * chunkSize, fileSize)
                            jobs.add(launch(pool) {
                                searchChunk(path, start, end, command, found)
                            })
                        }

                        found.await()
                    } finally {
                        jobs.forEach { it.cancelAndJoin() }
                        pool.close()
                    }
                }
            }

            if (result == null || found.getCompleted() == null) {
                throw AnnotationNotFoundException(
                    "Annotation not found for request: lap = ${command.lap}, rac = ${command.rac}, rap = ${command.rap}, refKey = ${command.refkey}"
                )
            }

            return@withContext found.getCompleted()
        }

    private fun searchChunk(
        filePath: Path,
        start: Long,
        end: Long,
        command: SearchAnnotationCommand,
        found: CompletableDeferred<Annotation?>
    ) {
        if (found.isCompleted) return

        FileChannel.open(filePath, StandardOpenOption.READ).use { channel ->
            channel.position(start)
            val buffer = ByteBuffer.allocate((end - start).toInt())
            channel.read(buffer)
            buffer.flip()
            val chunkData = CharBuffer.wrap(StandardCharsets.UTF_8.decode(buffer))

            chunkData.split("\n").forEach { line ->
                if (found.isCompleted) return@forEach

                val columns = line.split("\\s+".toRegex())
                if (columns.size < 8) return@forEach

                val rac = columns[0]
                val lap = columns[1]
                val rap = columns[2]
                val refkey = columns[3]
                val vcfId = columns[4]
                val clnsig = columns[5]
                val clnrevstat = columns[6]
                val clnvc = columns[7]

                if (rac == command.rac && lap == command.lap && rap == command.rap && refkey == command.refkey) {
                    found.complete(
                        Annotation(
                            rac = rac,
                            lap = lap,
                            rap = rap,
                            refkey = refkey,
                            vcfId = vcfId,
                            clnsig = clnsig,
                            clnrevstat = clnrevstat,
                            clnvc = clnvc
                        )
                    )
                }
            }
        }
    }
}
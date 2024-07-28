package example.com

import example.com.dtos.errors.ErrorDto
import example.com.exceptions.AnnotationNotFoundException
import example.com.exceptions.ValidationException
import example.com.plugins.configureSerialization
import example.com.routing.configureAnnotationRouting
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import java.nio.file.NoSuchFileException

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
    install(StatusPages) {
        exception<ValidationException> { call, cause ->
            val response = ErrorDto(
                statusCode = "VALIDATION_EXCEPTION",
                errorMessage = cause.localizedMessage,
                violations = cause.violations
            )
            call.respond(HttpStatusCode.BadRequest, response)
        }

        exception<NoSuchFileException> { call, cause ->
            val response = ErrorDto(
                statusCode = "NO_SUCH_FILE_EXCEPTION",
                errorMessage = cause.localizedMessage
            )
            call.respond(HttpStatusCode.InternalServerError, response)
        }

        exception<InternalError> { call, cause ->
            val response = ErrorDto(
                statusCode = "INTERNAL_SERVER_ERROR",
                errorMessage = cause.localizedMessage
            )
            call.respond(HttpStatusCode.InternalServerError, response)
        }

        exception<AnnotationNotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, cause.message ?: "Annotation not found")
        }
    }
    configureSerialization()
    configureAnnotationRouting()
}

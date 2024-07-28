package example.com.routing

import example.com.commands.SearchAnnotationCommand
import example.com.dtos.extensions.toAnnotationDto
import example.com.services.AnnotationService
import example.com.validators.AnnotationRequestValidator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


val service = AnnotationService()
fun Application.configureAnnotationRouting() {

    routing {
        get("/info") {
            val rac = call.request.queryParameters["rac"]
            val lap = call.request.queryParameters["lap"]
            val rap = call.request.queryParameters["rap"]
            val refkey = call.request.queryParameters["refkey"]

            AnnotationRequestValidator.validateInputParameters(rac, lap, rap, refkey)

            val command = SearchAnnotationCommand(rac!!, lap!!, rap!!, refkey!!)
            val annotation = service.findAnnotation(command)
            val annotationDto = annotation?.toAnnotationDto()

            if (annotationDto == null) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.OK, annotationDto)
            }
        }
    }
}

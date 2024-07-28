package example.com.dtos.responses

import kotlinx.serialization.Serializable

@Serializable
data class AnnotationDto(
    val rac: String,
    val lap: String,
    val rap: String,
    val refkey: String,
    val vcfId: String,
    val clnsig: String,
    val clnrevstat: String,
    val clnvc: String
)

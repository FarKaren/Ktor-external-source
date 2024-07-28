package example.com.dtos.errors

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDto(
    val statusCode: String,
    val errorMessage: String,
    val violations: List<Violation> = emptyList()
)

@Serializable
data class Violation(
    val fieldName: String,
    val errorCode: String
)

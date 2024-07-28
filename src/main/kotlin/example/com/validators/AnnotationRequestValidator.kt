package example.com.validators

import example.com.dtos.errors.Violation
import example.com.exceptions.ValidationException

object AnnotationRequestValidator {

    fun validateInputParameters(
         rac: String?,
         lap: String?,
         rap: String?,
         refkey: String?
    ) {
        val violations = mutableListOf<Violation>()

        if (rac == null) {
            violations.add(Violation(fieldName = "rac", errorCode = "NULL_ARGUMENT"))
        }
        if (lap == null) {
            violations.add(Violation(fieldName = "lap", errorCode = "NULL_ARGUMENT"))
        }
        if (rap == null) {
            violations.add(Violation(fieldName = "rap", errorCode = "NULL_ARGUMENT"))
        }
        if (refkey == null) {
            violations.add(Violation(fieldName = "refkey", errorCode = "NULL_ARGUMENT"))
        }

        if (violations.isNotEmpty()) {
            throw ValidationException(violations)
        }
    }
}
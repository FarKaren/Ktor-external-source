package example.com.exceptions

import example.com.dtos.errors.Violation

class ValidationException(val violations: List<Violation>) : RuntimeException()
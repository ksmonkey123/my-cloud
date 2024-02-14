package ch.awae.paas.service.auth.facade.rest

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = ex
            .bindingResult
            .fieldErrors
            .map { it.defaultMessage ?: "unknown error on field ${it.field}" }

        return ResponseEntity(
            mapOf("errors" to errors, "message" to "input validation failed"),
            HttpStatus.BAD_REQUEST
        )
    }

}
package ch.awae.mycloud.common

import jakarta.validation.*
import org.springframework.http.*
import org.springframework.jdbc.datasource.init.ScriptStatementFailedException
import org.springframework.web.bind.*
import org.springframework.web.bind.annotation.*

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = createLogger()

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

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleValidationErrors(ex: ConstraintViolationException): ResponseEntity<Map<String, Any>> {
        val errors = ex.constraintViolations
            .map { it.message }

        return ResponseEntity(
            mapOf("errors" to errors, "message" to "input validation failed"),
            HttpStatus.BAD_REQUEST
        )
    }


    @ExceptionHandler(IllegalArgumentException::class)
    fun handleValidationErrors(ex: IllegalArgumentException): ResponseEntity<Map<String, Any>> {
        return ResponseEntity(
            mapOf("message" to (ex.message ?: "IllegalArgumentException")),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleValidationErrors(ex: Exception): ResponseEntity<Map<String, Any>> {
        log.error(ex.message, ex)
        return ResponseEntity(
            mapOf("type" to ex.javaClass.name, "message" to (ex.message ?: ex.javaClass.simpleName)),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

}
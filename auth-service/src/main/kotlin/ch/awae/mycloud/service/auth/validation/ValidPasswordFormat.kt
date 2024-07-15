package ch.awae.mycloud.service.auth.validation

import jakarta.validation.*
import kotlin.reflect.*

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [PasswordFormatValidator::class])
annotation class ValidPasswordFormat(
    val message: String = "invalid password format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
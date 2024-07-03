package ch.awae.mycloud.service.auth.validation

import jakarta.validation.*

class PasswordFormatValidator : ConstraintValidator<ValidPasswordFormat, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true
        if (value.length < 8) return false

        return true
    }

}
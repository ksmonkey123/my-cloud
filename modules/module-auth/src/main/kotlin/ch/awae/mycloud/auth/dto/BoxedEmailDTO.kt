package ch.awae.mycloud.auth.dto

import jakarta.validation.constraints.Email
import java.util.*

data class BoxedEmailDTO(@field:Email val value: String?) {

    fun asOptional(): Optional<String> = Optional.ofNullable(value)

}
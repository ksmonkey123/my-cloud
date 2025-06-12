package ch.awae.mycloud.module.auth.dto

import ch.awae.mycloud.common.Boxed
import jakarta.validation.constraints.Email

data class BoxedEmailDTO(@field:Email val value: String?) {

    fun asBoxed() = Boxed(value)

}
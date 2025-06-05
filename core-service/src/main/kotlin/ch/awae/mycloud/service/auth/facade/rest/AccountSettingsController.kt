package ch.awae.mycloud.service.auth.facade.rest

import ch.awae.mycloud.common.auth.AuthInfo
import ch.awae.mycloud.common.auth.Language
import ch.awae.mycloud.service.auth.service.*
import ch.awae.mycloud.service.auth.validation.*
import jakarta.validation.*
import org.springframework.http.*
import org.springframework.security.access.prepost.*
import org.springframework.web.bind.annotation.*

@PreAuthorize("hasAuthority('user')")
@RequestMapping("/rest/auth/account")
@RestController
class AccountSettingsController(
    private val accountService: AccountService,
) {

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changeOwnPassword(@Valid @RequestBody request: ChangePasswordRequest) {
        accountService.changePassword(AuthInfo.username!!, request.oldPassword, request.newPassword)
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun editSettings(@Valid @RequestBody request: SettingsChangeRequest) {
        accountService.editAccount(
            AuthInfo.username!!,
            language = request.languageCode?.let { Language.fromCode(it) })
    }

    data class ChangePasswordRequest(
        val oldPassword: String,
        @field:ValidPasswordFormat val newPassword: String,
    )

    data class SettingsChangeRequest(
        val languageCode: String?,
    )

}
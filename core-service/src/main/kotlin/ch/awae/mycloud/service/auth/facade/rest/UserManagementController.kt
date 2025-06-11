package ch.awae.mycloud.service.auth.facade.rest

import ch.awae.mycloud.api.auth.Language
import ch.awae.mycloud.service.auth.dto.*
import ch.awae.mycloud.service.auth.service.*
import ch.awae.mycloud.service.auth.validation.*
import jakarta.validation.*
import org.springframework.http.*
import org.springframework.security.access.prepost.*
import org.springframework.web.bind.annotation.*

@PreAuthorize("hasAuthority('admin')")
@RequestMapping("/rest/auth/accounts")
@RestController
class UserManagementController(
    private val accountService: AccountService,
) {


    @GetMapping("")
    fun listAllAccounts(): List<AccountSummaryDto> {
        return accountService.getAccounts()
    }

    @PutMapping("/{username}")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAccount(
        @PathVariable username: String,
        @Valid @RequestBody request: CreateUserRequest
    ): AccountSummaryDto {
        return accountService.createAccount(
            username,
            request.password,
            request.admin,
            Language.fromCode(request.languageCode)
        )
    }

    @PatchMapping("/{username}")
    fun editAccount(
        @PathVariable username: String,
        @Valid @RequestBody request: PatchAccountRequest
    ): AccountSummaryDto {
        return accountService.editAccount(
            username,
            request.password,
            request.admin,
            request.enabled,
            request.languageCode?.let { Language.fromCode(it) })
    }

    @GetMapping("/{username}")
    fun getAccountDetails(@PathVariable username: String): AccountDetailsDto {
        return accountService.getAccountDetails(username)
    }

    @PatchMapping("/{username}/roles")
    fun editAccountRoles(@PathVariable username: String, @RequestBody request: PatchRolesRequest): AccountDetailsDto {
        return accountService.editAccountRoles(
            username,
            request.add ?: emptyList(),
            request.remove ?: emptyList()
        )
    }

    data class PatchRolesRequest(
        val add: List<String>?,
        val remove: List<String>?
    )


    data class CreateUserRequest(
        @field:ValidPasswordFormat val password: String,
        val admin: Boolean,
        val languageCode: String,
    )

    data class PatchAccountRequest(
        @field:ValidPasswordFormat val password: String?,
        val admin: Boolean?,
        val enabled: Boolean?,
        val languageCode: String?,
    )

}
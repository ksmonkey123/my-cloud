package ch.awae.mycloud.service.auth.service

import ch.awae.mycloud.common.ResourceAlreadyExistsException
import ch.awae.mycloud.common.ResourceNotFoundException
import ch.awae.mycloud.common.auth.Language
import ch.awae.mycloud.service.auth.domain.*
import ch.awae.mycloud.service.auth.dto.*
import ch.awae.mycloud.service.auth.exception.*
import ch.awae.mycloud.service.auth.validation.*
import jakarta.transaction.*
import org.hibernate.validator.constraints.*
import org.springframework.security.crypto.password.*
import org.springframework.stereotype.*

@Service
@Transactional
class AccountService(
    private val accountRepository: AccountRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun changePassword(
        username: String,
        oldPassword: String,
        @ValidPasswordFormat newPassword: String
    ) {
        val account = getAccount(username)

        if (!passwordEncoder.matches(oldPassword, account.password)) {
            throw InvalidPasswordException()
        }
        account.password = passwordEncoder.encode(newPassword)
    }

    fun createAccount(
        @Length(min = 5) username: String,
        @ValidPasswordFormat password: String,
        admin: Boolean,
        language: Language,
    ): AccountSummaryDto {
        if (accountRepository.existsByUsername(username))
            throw ResourceAlreadyExistsException("/accounts/$username")

        val account = Account(
            username,
            passwordEncoder.encode(password),
            true,
            admin,
            language,
        )

        return AccountSummaryDto(accountRepository.save(account))
    }

    @Throws(ResourceNotFoundException::class)
    fun getAccount(username: String): Account {
        return accountRepository.findByUsername(username) ?: throw ResourceNotFoundException("/accounts/$username")
    }

    fun getAccounts() = accountRepository.listAll().map(::AccountSummaryDto)

    fun editAccount(
        @Length(min = 5) username: String,
        @ValidPasswordFormat password: String? = null,
        admin: Boolean? = null,
        enabled: Boolean? = null,
        language: Language? = null,
    ): AccountSummaryDto {
        val account = getAccount(username)

        if (password != null) account.password = passwordEncoder.encode(password)
        if (enabled != null) account.enabled = enabled
        if (admin != null) account.admin = admin
        if (language != null) account.language = language

        return AccountSummaryDto(account)
    }

    fun getAccountDetails(username: String): AccountDetailsDto {
        return this.accountRepository.findByUsername(username)?.let(::AccountDetailsDto)
            ?: throw ResourceNotFoundException("/accounts/$username")
    }

    fun editAccountRoles(username: String, rolesToAdd: List<String>, rolesToRemove: List<String>): AccountDetailsDto {
        val account = this.accountRepository.findByUsername(username)
            ?: throw ResourceNotFoundException("/accounts/$username")

        account.roles.removeIf { it.name in rolesToRemove }
        account.roles.addAll(this.roleRepository.findRolesByName(rolesToAdd))

        return AccountDetailsDto(account)
    }

}

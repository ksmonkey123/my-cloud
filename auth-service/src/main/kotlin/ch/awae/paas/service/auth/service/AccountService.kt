package ch.awae.paas.service.auth.service

import ch.awae.paas.service.auth.*
import ch.awae.paas.service.auth.domain.*
import ch.awae.paas.service.auth.dto.*
import ch.awae.paas.service.auth.exception.*
import ch.awae.paas.service.auth.validation.*
import jakarta.transaction.*
import org.hibernate.validator.constraints.*
import org.springframework.security.crypto.password.*
import org.springframework.stereotype.*

@Service
@Transactional
class AccountService(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun changePassword(username: String, oldPassword: String, @ValidPasswordFormat newPassword: String) {
        val account = getAccount(username)

        if (!passwordEncoder.matches(oldPassword, account.password)) {
            throw InvalidPasswordException()
        }
        account.password = passwordEncoder.encode(newPassword)
    }

    fun createAccount(
        @Length(min = 5) username: String,
        @ValidPasswordFormat password: String, admin: Boolean
    ): AccountSummaryDto {
        if (accountRepository.existsByUsername(username))
            throw ResourceAlreadyExistsException("/accounts/$username")

        val account = Account(
            username,
            passwordEncoder.encode(password),
            true,
            admin
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
        @ValidPasswordFormat password: String?,
        admin: Boolean?,
        enabled: Boolean?,
    ): AccountSummaryDto {
        val account = getAccount(username)

        if (password != null) account.password = passwordEncoder.encode(password)
        if (enabled != null) account.enabled = enabled
        if (admin != null) account.admin = admin

        return AccountSummaryDto(account)
    }

}

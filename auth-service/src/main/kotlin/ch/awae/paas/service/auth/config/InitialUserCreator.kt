package ch.awae.paas.service.auth.config

import ch.awae.paas.*
import ch.awae.paas.service.auth.domain.*
import jakarta.transaction.Transactional
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class InitialUserCreator(
    val accountRepository: AccountRepository,
    val passwordEncoder: PasswordEncoder,
) : CommandLineRunner {

    private val logger = createLogger()

    @Transactional
    override fun run(vararg args: String?) {
        val adminCount = accountRepository.countAdmins()

        if (adminCount == 0L) {
            logger.warn("no administrator found. creating default 'admin'")
            accountRepository.save(
                Account(
                    username = "admin",
                    password = passwordEncoder.encode("admin"),
                    admin = true,
                )
            )
        } else {
            logger.info("found $adminCount administrator(s). no action required")
        }
    }

}
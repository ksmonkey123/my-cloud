package ch.awae.mycloud.service.auth.config

import ch.awae.mycloud.*
import ch.awae.mycloud.auth.*
import ch.awae.mycloud.service.auth.domain.*
import jakarta.transaction.Transactional
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class InitialUserCreator(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder,
) : CommandLineRunner {

    private val logger = createLogger()

    @Transactional
    override fun run(vararg args: String?) {
        val adminCount = accountRepository.countAdmins()

        if (adminCount == 0L) {
            logger.warn("no administrator found. creating default 'admin'")
            AuthInfo.impersonate("init") {
                accountRepository.save(
                    Account(
                        username = "admin",
                        password = passwordEncoder.encode("admin"),
                        admin = true,
                    )
                )
            }
        } else {
            logger.info("found $adminCount administrator(s). no action required")
        }
    }

}
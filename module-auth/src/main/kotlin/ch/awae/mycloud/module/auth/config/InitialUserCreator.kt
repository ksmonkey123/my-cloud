package ch.awae.mycloud.module.auth.config

import ch.awae.mycloud.api.auth.*
import ch.awae.mycloud.common.*
import ch.awae.mycloud.module.auth.domain.*
import jakarta.transaction.*
import org.springframework.boot.*
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.crypto.password.*
import org.springframework.stereotype.*

@Component
@Order(1)
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
                        language = Language.ENGLISH,
                        email = null,
                    )
                )
            }
        } else {
            logger.info("found $adminCount administrator(s). no action required")
        }
    }

}
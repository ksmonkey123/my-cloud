package ch.awae.mycloud.auth.config

import ch.awae.mycloud.auth.Language
import ch.awae.mycloud.common.util.createLogger
import ch.awae.mycloud.auth.domain.*
import jakarta.transaction.*
import org.springframework.boot.*
import org.springframework.core.annotation.*
import org.springframework.security.crypto.password.*
import org.springframework.stereotype.*

@Component
@Order(1)
class InitialUserCreator(
    private val accountRepository: ch.awae.mycloud.auth.domain.AccountRepository,
    private val passwordEncoder: PasswordEncoder,
) : CommandLineRunner {

    private val logger = createLogger()

    @Transactional
    override fun run(vararg args: String) {
        val adminCount = accountRepository.countAdmins()

        if (adminCount == 0L) {
            logger.warn("no administrator found. creating default 'admin'")
            accountRepository.save(
                _root_ide_package_.ch.awae.mycloud.auth.domain.Account(
                    username = "admin",
                    password = passwordEncoder.encode("admin")!!,
                    admin = true,
                    language = Language.ENGLISH,
                    email = null,
                )
            )
        } else {
            logger.info("found $adminCount administrator(s). no action required")
        }
    }

}
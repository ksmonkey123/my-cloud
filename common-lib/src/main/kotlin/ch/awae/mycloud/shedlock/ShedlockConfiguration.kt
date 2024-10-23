package ch.awae.mycloud.shedlock

import ch.awae.mycloud.*
import net.javacrumbs.shedlock.core.*
import net.javacrumbs.shedlock.provider.jdbctemplate.*
import net.javacrumbs.shedlock.spring.annotation.*
import net.javacrumbs.shedlock.support.*
import org.springframework.context.annotation.*
import org.springframework.jdbc.core.*
import javax.sql.*

@Configuration
@EnableSchedulerLock(
    defaultLockAtMostFor = "\${shedlock.default.max-lock:PT5M}",
    defaultLockAtLeastFor = "\${shedlock.default.min-lock:PT30S}",
)
class ShedlockConfiguration {

    val logger = createLogger()

    @Bean
    fun lockProvider(dataSource: DataSource): LockProvider {
        return JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(JdbcTemplate(dataSource))
                .usingDbTime()
                .build()
        ).also {
            logger.info("Shedlock hostname is '${Utils.getHostname()}'")
        }
    }
}

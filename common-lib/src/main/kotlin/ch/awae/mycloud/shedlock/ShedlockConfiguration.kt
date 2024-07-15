package ch.awae.mycloud.shedlock

import ch.awae.mycloud.*
import net.javacrumbs.shedlock.core.*
import net.javacrumbs.shedlock.provider.jdbctemplate.*
import net.javacrumbs.shedlock.spring.annotation.*
import net.javacrumbs.shedlock.support.Utils
import org.springframework.context.annotation.*
import org.springframework.jdbc.core.*
import javax.sql.*

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "\${shedlock.default-timeout:PT30S}")
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
            logger.info("Hostname is '${Utils.getHostname()}'")
        }
    }
}

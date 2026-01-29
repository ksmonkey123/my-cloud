package ch.awae.mycloud.config

import ch.awae.mycloud.common.createLogger
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import net.javacrumbs.shedlock.support.Utils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
@EnableSchedulerLock(
    defaultLockAtMostFor = "\${shedlock.default.max-lock:PT5M}",
    defaultLockAtLeastFor = "\${shedlock.default.min-lock:PT1S}",
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
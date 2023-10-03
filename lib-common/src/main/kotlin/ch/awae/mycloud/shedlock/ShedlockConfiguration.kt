package ch.awae.mycloud.shedlock

import net.javacrumbs.shedlock.core.*
import net.javacrumbs.shedlock.provider.jdbctemplate.*
import net.javacrumbs.shedlock.spring.annotation.*
import org.springframework.context.annotation.*
import org.springframework.jdbc.core.*
import javax.sql.*

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "\${shedlock.default-timeout:PT30S}")
class ShedlockConfiguration {
    @Bean
    fun lockProvider(dataSource: DataSource): LockProvider {
        return JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(JdbcTemplate(dataSource))
                .usingDbTime()
                .build()
        )
    }
}

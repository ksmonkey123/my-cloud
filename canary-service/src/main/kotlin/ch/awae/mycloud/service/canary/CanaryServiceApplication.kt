package ch.awae.mycloud.service.canary

import ch.awae.mycloud.audit.*
import ch.awae.mycloud.auth.*
import ch.awae.mycloud.shedlock.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.scheduling.annotation.*
import org.springframework.transaction.annotation.*


@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@EnableShedlock
@EnableClientSecurity
@EnableAuditLog
@ConfigurationPropertiesScan
class CanaryServiceApplication

fun main(args: Array<String>) {
    runApplication<CanaryServiceApplication>(*args)
}

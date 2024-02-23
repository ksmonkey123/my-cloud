package ch.awae.paas.service.audit

import ch.awae.paas.audit.*
import ch.awae.paas.auth.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.context.annotation.*
import org.springframework.kafka.annotation.*
import org.springframework.transaction.annotation.*

@SpringBootApplication
@EnableKafka
@EnableTransactionManagement
@Import(ClientSecurityConfiguration::class, AuditLogConfiguration::class)
class AuditServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(AuditServiceApplication::class.java, *args)
}
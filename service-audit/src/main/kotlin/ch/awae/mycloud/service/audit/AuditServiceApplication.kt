package ch.awae.mycloud.service.audit

import ch.awae.mycloud.audit.*
import ch.awae.mycloud.auth.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.kafka.annotation.*
import org.springframework.transaction.annotation.*

@SpringBootApplication
@EnableKafka
@EnableTransactionManagement
@EnableAuditLog
@EnableClientSecurity
class AuditServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(AuditServiceApplication::class.java, *args)
}
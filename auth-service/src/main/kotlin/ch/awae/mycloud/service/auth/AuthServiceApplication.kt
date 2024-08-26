package ch.awae.mycloud.service.auth

import ch.awae.mycloud.audit.*
import ch.awae.mycloud.auth.*
import ch.awae.mycloud.shedlock.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.*

@SpringBootApplication
@EnableTransactionManagement
@EnableAuditLog
@EnableServiceSecurity
@EnableScheduling
@EnableShedlock
class AuthServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(AuthServiceApplication::class.java, *args)
}

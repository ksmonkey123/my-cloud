package ch.awae.mycloud.service.shortener

import ch.awae.mycloud.audit.*
import ch.awae.mycloud.auth.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.transaction.annotation.*

@SpringBootApplication
@EnableTransactionManagement
@EnableAuditLog
@EnableClientSecurity
class ShortenerServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(ShortenerServiceApplication::class.java, *args)
}

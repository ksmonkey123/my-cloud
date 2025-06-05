package ch.awae.mycloud.service

import ch.awae.mycloud.auth.*
import ch.awae.mycloud.shedlock.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.*
import org.springframework.transaction.annotation.*

@SpringBootApplication
@EnableTransactionManagement
@EnableServiceSecurity
@EnableScheduling
@EnableShedlock
@EnableJpaRepositories
class CoreServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(CoreServiceApplication::class.java, *args)
}

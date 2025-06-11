package ch.awae.mycloud

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.context.properties.*
import org.springframework.data.jpa.repository.config.*
import org.springframework.scheduling.annotation.*
import org.springframework.transaction.annotation.*

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@EnableJpaRepositories
@ConfigurationPropertiesScan
class CoreServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(CoreServiceApplication::class.java, *args)
}


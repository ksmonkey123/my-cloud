package ch.awae.mycloud.service.canary

import ch.awae.mycloud.auth.*
import ch.awae.mycloud.notification.*
import ch.awae.mycloud.shedlock.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.context.properties.*
import org.springframework.scheduling.annotation.*
import org.springframework.transaction.annotation.*


@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@EnableShedlock
@EnableClientSecurity
@EnableNotification
@ConfigurationPropertiesScan
class CanaryServiceApplication

fun main(args: Array<String>) {
    runApplication<CanaryServiceApplication>(*args)
}

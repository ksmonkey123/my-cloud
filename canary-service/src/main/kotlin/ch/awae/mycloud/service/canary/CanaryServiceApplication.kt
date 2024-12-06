package ch.awae.mycloud.service.canary

import ch.awae.mycloud.auth.*
import ch.awae.mycloud.shedlock.*
import ch.awae.mycloud.telegram_notification.*
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
@EnableTelegramNotification
@ConfigurationPropertiesScan
class CanaryServiceApplication

fun main(args: Array<String>) {
    runApplication<CanaryServiceApplication>(*args)
}

package ch.awae.mycloud.service.telegram_notification

import ch.awae.mycloud.auth.*
import ch.awae.mycloud.kafka.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.context.properties.*

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableClientSecurity
@EnableKafkaDefaults
class TelegramNotificationServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(TelegramNotificationServiceApplication::class.java, *args)
}

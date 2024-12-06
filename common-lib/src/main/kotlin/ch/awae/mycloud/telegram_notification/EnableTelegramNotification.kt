package ch.awae.mycloud.telegram_notification

import ch.awae.mycloud.kafka.*
import org.springframework.context.annotation.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@EnableKafkaDefaults
@Import(TelegramNotificationService::class)
annotation class EnableTelegramNotification

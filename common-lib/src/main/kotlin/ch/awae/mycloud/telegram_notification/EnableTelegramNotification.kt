package ch.awae.mycloud.telegram_notification

import ch.awae.mycloud.kafka.*
import org.springframework.context.annotation.*

@Deprecated("use ch.awae.mycloud.notification.* instead")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@EnableKafkaDefaults
@Import(TelegramNotificationService::class)
annotation class EnableTelegramNotification

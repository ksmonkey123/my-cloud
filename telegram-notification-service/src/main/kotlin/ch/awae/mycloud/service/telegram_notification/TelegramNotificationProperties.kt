package ch.awae.mycloud.service.telegram_notification

import org.springframework.boot.context.properties.*

@ConfigurationProperties(prefix = "telegram-notification")
data class TelegramNotificationProperties(
    val botToken: String,
    val chatId: String,
    val footer: String,
)

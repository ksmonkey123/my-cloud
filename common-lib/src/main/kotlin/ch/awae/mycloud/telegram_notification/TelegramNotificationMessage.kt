package ch.awae.mycloud.telegram_notification

@Deprecated("use ch.awae.mycloud.notification.* instead")
enum class TelegramMessageFormat {
    HTML,
    @Deprecated("only html will be supported going forward")
    MARKDOWN
}

@Deprecated("use ch.awae.mycloud.notification.* instead")
data class TelegramNotificationMessage(val text: String, val format: TelegramMessageFormat)
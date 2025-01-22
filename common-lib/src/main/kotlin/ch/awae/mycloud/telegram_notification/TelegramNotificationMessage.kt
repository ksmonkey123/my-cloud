package ch.awae.mycloud.telegram_notification

enum class TelegramMessageFormat {
    HTML,
    @Deprecated("only html will be supported going forward")
    MARKDOWN
}

data class TelegramNotificationMessage(val text: String, val format: TelegramMessageFormat)
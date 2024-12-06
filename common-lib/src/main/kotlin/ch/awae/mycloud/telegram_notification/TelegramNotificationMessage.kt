package ch.awae.mycloud.telegram_notification

enum class TelegramMessageFormat {
    HTML, MARKDOWN
}

data class TelegramNotificationMessage(val text: String, val format: TelegramMessageFormat)
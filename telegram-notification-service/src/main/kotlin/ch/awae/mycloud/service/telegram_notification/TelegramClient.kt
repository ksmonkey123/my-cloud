package ch.awae.mycloud.service.telegram_notification

import ch.awae.mycloud.rest.*
import ch.awae.mycloud.telegram_notification.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.client.*

@Component
class TelegramClient(@External val http: RestTemplate, config: TelegramNotificationProperties) {

    init {
        if (!config.botToken.matches(Regex("[0-9]+:[a-zA-Z0-9_-]{35}"))) {
            throw IllegalArgumentException("invalid telegram token. bad format")
        }
    }

    val url = "https://api.telegram.org/bot${config.botToken}/sendMessage"
    val chat = config.chatId
    val footer = config.footer

    fun sendMessage(message: String, format: TelegramMessageFormat) {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val map = when (format) {
            TelegramMessageFormat.HTML -> mapOf(
                "chat_id" to chat,
                "text" to "$message\n\n--$footer",
                "parse_mode" to "HTML"
            )

            TelegramMessageFormat.MARKDOWN -> mapOf(
                "chat_id" to chat,
                "text" to "$message\n\n\\-\\-${escapeFooter(footer)}",
                "parse_mode" to "MarkdownV2"
            )
        }

        val entity = HttpEntity(map, headers)

        http.postForEntity(url, entity, Any::class.java)
    }

    val charsToEscape = listOf('_', '*', '[', ']', '(', ')', '~', '`', '>', '#', '+', '-', '=', '|', '{', '}', '.', '!')

    private fun escapeFooter(footer: String): String {
        var temp = footer
        for (char in charsToEscape) {
            temp = temp.replace("$char", "\\$char")
        }
        return temp
    }

}
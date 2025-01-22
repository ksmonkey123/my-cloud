package ch.awae.mycloud.service.telegram_notification

import ch.awae.mycloud.rest.*
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

    fun sendMessage(message: String) {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val map = mapOf(
            "chat_id" to chat,
            "text" to "$message\n\n--$footer",
            "parse_mode" to "HTML"
        )

        val entity = HttpEntity(map, headers)

        http.postForEntity(url, entity, Any::class.java)
    }

}
package ch.awae.mycloud.serivce.ytdl_telegram_adapter

import ch.awae.mycloud.*
import ch.awae.mycloud.telegram.*
import ch.awae.mycloud.telegram.response.*
import org.springframework.beans.factory.annotation.*
import org.springframework.stereotype.*

@Component
class YtdlTelegramBot(@Value("\${ytdl-telegram.bot-token}") token: String) : TelegramBot<Long>(token) {

    private val logger = createLogger()

    override fun resolveTelegramUserId(userId: Long): Long? {
        return userId
    }

    override fun onMessage(text: String, context: EventContext<Long>): Response {
        logger.info("onMessage ${context.user}: $text")
        if (text == "/test") {
            return CallbackResponse(
                "please choose",
                listOf(
                    CallbackOption("Option 1", "_opt1"),
                    CallbackOption("Option 2", "_opt2"),
                    CallbackOption("Option 3", "_opt3"),
                    CallbackOption("Option 4", "_opt4"),
                    CallbackOption("Option 5", "_opt5"),
                ),
                columns = 2,
                reply = true
            )
        }
        return MessageResponse("invalid command")
    }

    override fun onCallbackQuery(data: String, context: EventContext<Long>): Response? {
        logger.info("onCallbackQuery: ${context.user}: $data")
        return MessageResponse("ok: $data")
    }

}
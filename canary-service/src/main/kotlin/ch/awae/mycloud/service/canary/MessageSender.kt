package ch.awae.mycloud.service.canary

import ch.awae.mycloud.*
import ch.awae.mycloud.telegram_notification.*
import org.springframework.stereotype.*

@Service
class MessageSender(
    private val telegramNotificationService: TelegramNotificationService,
) {
    private val logger = createLogger()

    fun sendMessage(message: String) {
        logger.info("sending message {}", message)
        telegramNotificationService.send(TelegramNotificationMessage(message, TelegramMessageFormat.HTML))
    }

}

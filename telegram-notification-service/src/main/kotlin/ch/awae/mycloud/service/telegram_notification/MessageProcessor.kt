package ch.awae.mycloud.service.telegram_notification

import ch.awae.mycloud.telegram_notification.*
import org.springframework.kafka.annotation.*
import org.springframework.stereotype.*
import java.util.logging.*

@Component
@KafkaListener(topics = [TELEGRAM_NOTIFICATION_TOPIC], containerFactory = "kafkaListenerContainerFactory")
class MessageProcessor(val client: TelegramClient) {

    val logger: Logger = Logger.getLogger(javaClass.name)

    @KafkaHandler
    fun handleNotificationMessage(message: TelegramNotificationMessage) {
        logger.info("handling ${message.format} message: \"${message.text.replace("\n", "\\n")}\"")
        client.sendMessage(message.text, message.format)
    }

}
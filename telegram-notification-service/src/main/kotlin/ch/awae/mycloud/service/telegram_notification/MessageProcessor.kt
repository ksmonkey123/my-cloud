package ch.awae.mycloud.service.telegram_notification

import ch.awae.mycloud.notification.*
import ch.awae.mycloud.telegram_notification.*
import org.springframework.kafka.annotation.*
import org.springframework.stereotype.*
import java.util.logging.*

@Component
@KafkaListener(topics = [NOTIFICATION_TOPIC, TELEGRAM_NOTIFICATION_TOPIC], containerFactory = "kafkaListenerContainerFactory")
class MessageProcessor(val client: TelegramClient) {

    val logger: Logger = Logger.getLogger(javaClass.name)

    @KafkaHandler
    fun handleTelegramNotificationMessage(message: TelegramNotificationMessage) {
        logger.info("handling telegram ${message.format} message: \"${message.text.replace("\n", "\\n")}\"")
        client.sendMessage(message.text, message.format)
    }

    @KafkaHandler
    fun handleNotificationMessage(message: NotificationMessage) {
        logger.info("handling notification message: \"${message.text.replace("\n", "\\n")}\"")
        client.sendMessage(message.title + "\n\n" + message.text, TelegramMessageFormat.HTML)
    }


}
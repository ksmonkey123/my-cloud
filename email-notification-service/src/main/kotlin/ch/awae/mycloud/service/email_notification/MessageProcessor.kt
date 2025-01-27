package ch.awae.mycloud.service.email_notification

import ch.awae.mycloud.notification.*
import org.springframework.kafka.annotation.*
import org.springframework.stereotype.*
import java.util.logging.*

@Component
@KafkaListener(topics = [NOTIFICATION_TOPIC], containerFactory = "kafkaListenerContainerFactory")
class MessageProcessor(val client: EmailClient) {

    val logger: Logger = Logger.getLogger(javaClass.name)

    @KafkaHandler
    fun handleNotificationMessage(message: NotificationMessage) {
        logger.info("handling notification message: $message")
        client.sendPlainMessage(message)
    }

}

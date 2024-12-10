package ch.awae.mycloud.telegram_notification

import org.springframework.kafka.core.*
import org.springframework.stereotype.*

const val TELEGRAM_NOTIFICATION_TOPIC = "mycloud.telegram-notification"

@Service
class TelegramNotificationService(private val kafka: KafkaTemplate<String, Any>) {

    fun send(message: TelegramNotificationMessage) {
        kafka.send(TELEGRAM_NOTIFICATION_TOPIC, message).get()
    }

}
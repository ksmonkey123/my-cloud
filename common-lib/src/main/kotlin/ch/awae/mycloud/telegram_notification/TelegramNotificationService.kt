package ch.awae.mycloud.telegram_notification

import org.springframework.kafka.core.*
import org.springframework.stereotype.*

@Deprecated("use ch.awae.mycloud.notification.* instead")
const val TELEGRAM_NOTIFICATION_TOPIC = "mycloud.telegram-notification"

@Deprecated("use ch.awae.mycloud.notification.* instead")
@Service
class TelegramNotificationService(private val kafka: KafkaTemplate<String, Any>) {

    fun send(message: TelegramNotificationMessage) {
        kafka.send(TELEGRAM_NOTIFICATION_TOPIC, message).get()
    }

}
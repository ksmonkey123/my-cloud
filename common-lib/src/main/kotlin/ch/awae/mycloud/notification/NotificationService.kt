package ch.awae.mycloud.notification

import org.springframework.kafka.core.*
import org.springframework.stereotype.*

const val NOTIFICATION_TOPIC = "mycloud.notification"

@Service
class NotificationService(private val kafka: KafkaTemplate<String, Any>) {

    fun send(message: NotificationMessage) {
        kafka.send(NOTIFICATION_TOPIC, message).get()
    }

}
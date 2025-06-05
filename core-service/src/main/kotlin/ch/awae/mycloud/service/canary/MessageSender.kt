package ch.awae.mycloud.service.canary

import ch.awae.mycloud.*
import ch.awae.mycloud.notification.*
import org.springframework.stereotype.*

@Service
class MessageSender(
    private val notificationService: NotificationService,
) {
    private val logger = createLogger()

    fun sendMessage(title: String, message: String) {
        logger.info("sending message {}", message)
        notificationService.send(NotificationMessage(title, message))
    }

}

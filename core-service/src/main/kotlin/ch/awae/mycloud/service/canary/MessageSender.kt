package ch.awae.mycloud.service.canary

import ch.awae.mycloud.common.createLogger
import ch.awae.mycloud.common.notification.NotificationMessage
import ch.awae.mycloud.common.notification.NotificationService
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

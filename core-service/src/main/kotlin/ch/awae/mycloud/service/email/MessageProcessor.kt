package ch.awae.mycloud.service.email

import ch.awae.mycloud.common.notification.NotificationMessage
import ch.awae.mycloud.common.notification.NotificationService
import org.springframework.stereotype.*
import java.util.logging.*

@Service
class EmailNotificationService(val client: EmailClient) : NotificationService {
    val logger: Logger = Logger.getLogger(javaClass.name)

    override fun send(message: NotificationMessage) {
        logger.info("handling notification message: $message")
        client.sendPlainMessage(message)
    }
}

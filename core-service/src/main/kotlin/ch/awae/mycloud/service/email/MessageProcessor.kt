package ch.awae.mycloud.service.email

import ch.awae.mycloud.api.email.*
import ch.awae.mycloud.api.notification.*
import org.springframework.boot.context.properties.*
import org.springframework.stereotype.*

@ConfigurationProperties(prefix = "email-notification")
data class EmailNotificationProperties(
    val from: String,
    val to: String,
    val footer: String,
)

@Service
class EmailNotificationService(
    val properties: EmailNotificationProperties,
    val emailService: EmailSendService,
) : NotificationService {
    override fun sendPlaintextNotification(message: NotificationMessage) {
        emailService.send(
            EmailMessage(
                sender = properties.from,
                recipient = properties.to,
                subject = message.title,
                body = PlainBody(message.text),
            )
        )
    }
}

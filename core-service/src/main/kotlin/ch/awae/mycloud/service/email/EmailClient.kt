package ch.awae.mycloud.service.email

import ch.awae.mycloud.common.createLogger
import ch.awae.mycloud.common.notification.NotificationMessage
import com.mailjet.client.*
import com.mailjet.client.transactional.*
import org.springframework.boot.context.properties.*
import org.springframework.stereotype.*

@ConfigurationProperties(prefix = "email-notification")
data class EmailNotificationProperties(
    val from: String,
    val to: String,
    val footer: String,
)

@Component
class EmailClient(
    val properties: EmailNotificationProperties,
    val mailjetClient: MailjetClient,
) {

    val log = createLogger()

    fun sendPlainMessage(message: NotificationMessage) {
        log.info("sending plain message {}", message)

        val mail = TransactionalEmail.builder()
            .to(SendContact(properties.to))
            .from(SendContact(properties.from))
            .subject(message.title)
            .textPart(message.text + "\n\n -- " + properties.footer)
            .build()

        val req = SendEmailsRequest.builder().message(mail).build()

        req.sendWith(mailjetClient)
    }

}

package ch.awae.mycloud.email.mailjet

import ch.awae.mycloud.common.ResourceNotFoundException
import ch.awae.mycloud.common.util.createLogger
import ch.awae.mycloud.email.outbox.EmailBodyFormat
import ch.awae.mycloud.email.outbox.EmailOutboxRepository
import com.mailjet.client.MailjetClient
import com.mailjet.client.transactional.SendContact
import com.mailjet.client.transactional.SendEmailsRequest
import com.mailjet.client.transactional.TransactionalEmail
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@Transactional
class MailjetSender(
    val repo: EmailOutboxRepository,
    val mailjetClient: MailjetClient,
    @param:Value("\${email.sender}") private val sender: String,
) {

    val log = createLogger()

    fun sendMail(id: Long) {
        val email = repo.findToSend(id) ?: throw ResourceNotFoundException("/email/$id")

        log.info("sending email: {}", email)

        val mail = TransactionalEmail.builder()
            .to(SendContact(email.recipient))
            .from(SendContact(sender))
            .subject(email.subject)
            .let {
                when (email.bodyFormat) {
                    EmailBodyFormat.HTML -> it.htmlPart(email.bodyContent)
                    EmailBodyFormat.TEXT -> it.textPart(email.bodyContent)
                    EmailBodyFormat.MARKDOWN -> it.htmlPart(MarkdownParser.toHtml(email.bodyContent))
                }
            }
            .build()

        val req = SendEmailsRequest.builder().message(mail).build()
        req.sendWith(mailjetClient)

        email.markAsSent()
    }

}

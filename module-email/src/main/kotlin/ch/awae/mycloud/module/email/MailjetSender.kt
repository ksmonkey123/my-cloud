package ch.awae.mycloud.module.email

import ch.awae.mycloud.common.*
import com.mailjet.client.*
import com.mailjet.client.transactional.*
import jakarta.transaction.*
import org.springframework.stereotype.*

@Service
@Transactional
class MailjetSender(
    val repo: EmailOutboxRepository,
    val mailjetClient: MailjetClient
) {

    val log = createLogger()

    fun sendMail(id: Long) {
        val email = repo.findToSend(id) ?: throw ResourceNotFoundException("/email/$id")

        log.info("sending email: {}", email)

        val mail = TransactionalEmail.builder()
            .to(SendContact(email.recipient))
            .from(SendContact(email.sender))
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

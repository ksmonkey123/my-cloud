package ch.awae.mycloud.module.email

import ch.awae.mycloud.api.email.*
import ch.awae.mycloud.common.*
import jakarta.transaction.*
import org.springframework.stereotype.*

@Transactional
@Service
class EmailSendServiceImpl(val repo: EmailOutboxRepository) : EmailSendService {

    private val logger = createLogger()

    override fun send(email: EmailMessage) {
        val (bodyFormat, bodyContent) = extractBody(email.body)

        val stored = repo.save(
            EmailOutbox(
                sender = email.sender,
                recipient = email.recipient,
                subject = email.subject,
                bodyFormat = bodyFormat,
                bodyContent = bodyContent,
            )
        )

        logger.info("email recorded for sending: {}", stored)
    }

    private fun extractBody(body: EmailBody): Pair<EmailBodyFormat, String> {
        return when (body) {
            is PlainBody -> Pair(EmailBodyFormat.TEXT, body.text)
            is HtmlBody -> Pair(EmailBodyFormat.HTML, body.text)
            is MarkdownBody -> Pair(EmailBodyFormat.MARKDOWN, body.text)
        }
    }

}
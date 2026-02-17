package ch.awae.mycloud.module.email

import ch.awae.mycloud.email.EmailMessage
import ch.awae.mycloud.email.EmailSendService
import ch.awae.mycloud.common.util.createLogger
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Transactional
@Service
class EmailSendServiceImpl(val repo: EmailOutboxRepository) : EmailSendService {

    private val logger = createLogger()

    override fun send(email: EmailMessage) : Boolean{
        val (bodyFormat, bodyContent) = extractBody(email.body)

        email.uid?.let {
            if (repo.existsByMessageUid(it)) {
                logger.warn("email with uid {} already exists, skipping", email.uid)
                return false
            }
        }

        val stored = repo.save(
            EmailOutbox(
                recipient = email.recipient,
                subject = email.subject,
                bodyFormat = bodyFormat,
                bodyContent = bodyContent,
                messageUid = email.uid
            )
        )

        logger.info("email recorded for sending: {}", stored)
        return true
    }

    private fun extractBody(body: EmailMessage.Body): Pair<EmailBodyFormat, String> {
        return when (body) {
            is EmailMessage.PlaintextBody -> Pair(EmailBodyFormat.TEXT, body.text)
            is EmailMessage.HtmlBody -> Pair(EmailBodyFormat.HTML, body.text)
            is EmailMessage.MarkdownBody -> Pair(EmailBodyFormat.MARKDOWN, body.text)
        }
    }

}
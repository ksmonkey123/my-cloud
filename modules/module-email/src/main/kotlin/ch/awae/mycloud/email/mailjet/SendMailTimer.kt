package ch.awae.mycloud.email.mailjet

import ch.awae.mycloud.common.util.createLogger
import ch.awae.mycloud.email.outbox.EmailOutboxRepository
import net.javacrumbs.shedlock.spring.annotation.*
import org.springframework.scheduling.annotation.*
import org.springframework.stereotype.*

@Controller
class SendMailTimer(val repo: EmailOutboxRepository, val client: MailjetSender) {

    private val logger = createLogger()

    @SchedulerLock(name = "email:sender")
    @Scheduled(cron = "\${email.timer.send}")
    fun sendMails() {
        val ids = repo.listToSend(100)
            .takeIf { it.isNotEmpty() } ?: return

        logger.info("sending ${ids.size} emails")
        for (id in ids) {
            try {
                client.sendMail(id)
            } catch (ex: Exception) {
                logger.error("failed to send email message", ex)
            }
        }
    }
}
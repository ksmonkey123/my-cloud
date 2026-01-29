package ch.awae.mycloud.module.email.linuxmail

import ch.awae.mycloud.api.email.EmailMessage
import ch.awae.mycloud.api.email.EmailSendService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class LinuxMailService(private val sendService: EmailSendService) {

    fun forwardMail(recipient: String, content: String) {
        val headers = extractHeaders(content)

        sendService.send(
            EmailMessage(
                subject = headers["SUBJECT"] ?: "LINUX NOTIFICATION",
                recipient = recipient,
                body = EmailMessage.PlaintextBody(content),
                uid = headers["MESSAGE-ID"],
            )
        )
    }

    private fun extractHeaders(content: String): Map<String, String> {
        val lines = content.lines().drop(1)
        val headers = mutableMapOf<String, String>()

        var currentKey: String? = null
        var currentValue: StringBuilder? = null

        for (line in lines) {
            if (line.isBlank()) break // Header section ends at first empty line

            if (line.startsWith(" ") || line.startsWith("\t")) {
                // Folded line
                if (currentKey != null && currentValue != null) {
                    currentValue.append(" ").append(line.trim())
                }
            } else {
                // New header line
                if (currentKey != null && currentValue != null) {
                    headers[currentKey] = currentValue.toString()
                }

                val colonIndex = line.indexOf(':')
                if (colonIndex != -1) {
                    currentKey = line.substring(0, colonIndex).trim().uppercase()
                    currentValue = StringBuilder(line.substring(colonIndex + 1).trim())
                } else {
                    currentKey = null
                    currentValue = null
                }
            }
        }

        if (currentKey != null && currentValue != null) {
            headers[currentKey] = currentValue.toString()
        }

        return headers
    }

}
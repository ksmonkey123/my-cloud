package ch.awae.mycloud.email.outbox

import ch.awae.mycloud.email.EmailMessage
import ch.awae.mycloud.test.ModuleTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EmailSendServiceImplTest : ModuleTest() {

    @Autowired
    lateinit var repository: EmailOutboxRepository

    @Autowired
    lateinit var service: EmailSendServiceImpl

    @Test
    fun `sending a message works`() {
        // arrange
        repository.deleteAll()
        val email = EmailMessage(
            "dummy@example.org", "This is a test message",
            EmailMessage.PlaintextBody("This is a text message")
        )

        // act
        assertTrue(service.send(email))

        // assert
        val emails = repository.findAll()
        assertEquals(1, emails.size)
        val savedMail = emails.first()
        assertEquals("dummy@example.org", savedMail.recipient)
        assertEquals("This is a test message", savedMail.subject)
        assertEquals("This is a text message", savedMail.bodyContent)
        assertEquals(EmailBodyFormat.TEXT, savedMail.bodyFormat)
        assertNull(savedMail.messageUid)
    }

    @Test
    fun `sending message without uid twice creates 2 messages`() {
        // arrange
        repository.deleteAll()
        val email = EmailMessage(
            "dummy@example.org", "This is a test message",
            EmailMessage.PlaintextBody("This is a text message")
        )

        // act
        assertTrue(service.send(email))
        assertTrue(service.send(email))

        // assert
        val emails = repository.findAll()
        assertEquals(2, emails.size)
    }

    @Test
    fun `sending message with uid twice creates 1 message`() {
        // arrange
        repository.deleteAll()
        val email = EmailMessage(
            "dummy@example.org", "This is a test message",
            EmailMessage.PlaintextBody("This is a text message"),
            uid = "message-1"
        )

        // act
        assertTrue(service.send(email))
        assertFalse(service.send(email))

        // assert
        val emails = repository.findAll()
        assertEquals(1, emails.size)
    }

}
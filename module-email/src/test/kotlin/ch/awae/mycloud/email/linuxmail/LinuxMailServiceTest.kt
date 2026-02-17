package ch.awae.mycloud.email.linuxmail

import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LinuxMailServiceTest {

    private val linuxMailService = LinuxMailService(mockk())

    // Using reflection to test private method
    private fun callExtractHeaders(content: String): Map<String, String> {
        val method = LinuxMailService::class.java.getDeclaredMethod("extractHeaders", String::class.java)
        method.isAccessible = true
        @Suppress("UNCHECKED_CAST") return method.invoke(linuxMailService, content) as Map<String, String>
    }

    @Test
    fun `extractHeaders should extract all headers`() {
        val content = """
            Ignored First Line
            From: sender@example.com
            To: recipient@example.com
            Subject: Hello World
            X-Custom-Header: custom value
            
            This is the body.
        """.trimIndent()

        val headers = callExtractHeaders(content)
        assertEquals("sender@example.com", headers["FROM"])
        assertEquals("recipient@example.com", headers["TO"])
        assertEquals("Hello World", headers["SUBJECT"])
        assertEquals("custom value", headers["X-CUSTOM-HEADER"])
    }

    @Test
    fun `extractHeaders should handle folded headers`() {
        val content = """
            Ignored First Line
            Subject: This is a very
             long subject line
            X-Long-Header: line 1
                line 2
            
            This is the body.
        """.trimIndent()

        val headers = callExtractHeaders(content)
        assertEquals("This is a very long subject line", headers["SUBJECT"])
        assertEquals("line 1 line 2", headers["X-LONG-HEADER"])
    }
}

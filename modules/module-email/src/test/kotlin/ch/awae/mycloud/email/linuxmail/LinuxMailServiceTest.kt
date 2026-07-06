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

    private fun callDecodeBody(content: String): String {
        val method = LinuxMailService::class.java.getDeclaredMethod("decodeBody", String::class.java)
        method.isAccessible = true
        return method.invoke(linuxMailService, content) as String
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

    @Test
    fun `decodeBody should replace =3D with =`() {
        val content = "This is an =3D equals sign"
        assertEquals("This is an = equals sign", callDecodeBody(content))
    }

    @Test
    fun `decodeBody should remove soft line breaks`() {
        val content = "This is a long line that=\r\ncontinues on next line and =\ncontinues here."
        assertEquals("This is a long line thatcontinues on next line and continues here.", callDecodeBody(content))
    }

    @Test
    fun `decodeBody should handle combined cases`() {
        val content = "Some =3D text with a =\r\nsoft break."
        assertEquals("Some = text with a soft break.", callDecodeBody(content))
    }
}

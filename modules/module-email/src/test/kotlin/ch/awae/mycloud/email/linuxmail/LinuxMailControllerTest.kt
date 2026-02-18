package ch.awae.mycloud.email.linuxmail

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import io.mockk.mockk
import java.io.ByteArrayInputStream
import kotlin.random.Random
import kotlin.test.assertContentEquals
import java.io.InputStream

class LinuxMailControllerTest {

    private val controller = LinuxMailController(mockk())

    private fun callSaveStreamRead(stream: InputStream, expectedSize: Long): ByteArray {
        val method = LinuxMailController::class.java.getDeclaredMethod("saveStreamRead", InputStream::class.java, Long::class.java)
        method.isAccessible = true
        return method.invoke(controller, stream, expectedSize) as ByteArray
    }

    @Test
    fun `saveStreamRead with valid input should return normally`() {
        // arrange
        val buffer = Random.nextBytes(1234)
        val stream = ByteArrayInputStream(buffer)

        // act
        val result = callSaveStreamRead(stream, 1234)

        // assert
        assertContentEquals(buffer, result)
    }

    @Test
    fun `saveStreamRead with short stream should throw`() {
        // arrange
        val buffer = Random.nextBytes(1234)
        val stream = ByteArrayInputStream(buffer)

        // act & assert
        assertThrows<Exception> { callSaveStreamRead(stream, 2000) }
    }

    @Test
    fun `saveStreamRead with long stream should throw`() {
        // arrange
        val buffer = Random.nextBytes(1234)
        val stream = ByteArrayInputStream(buffer)

        // act & assert
        assertThrows<Exception> { callSaveStreamRead(stream, 1000) }
    }

    @Test
    fun `saveStreamRead limited to int lenghts`() {
        // arrange
        val buffer = Random.nextBytes(1234)
        val stream = ByteArrayInputStream(buffer)

        // act & assert
        assertThrows<Exception> { callSaveStreamRead(stream, Int.MAX_VALUE + 1L) }
    }

    @Test
    fun `saveStreamRead rejects negative size`() {
        // arrange
        val buffer = Random.nextBytes(1234)
        val stream = ByteArrayInputStream(buffer)

        // act & assert
        assertThrows<Exception> { callSaveStreamRead(stream, -1) }
    }

}
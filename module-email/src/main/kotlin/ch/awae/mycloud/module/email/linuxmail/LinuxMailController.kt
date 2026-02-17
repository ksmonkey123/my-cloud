package ch.awae.mycloud.module.email.linuxmail

import ch.awae.mycloud.auth.AuthInfo
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.io.InputStream

/** max mail size: 10MiB */
const val MAX_MAIL_SIZE_BYTES = 10485760L

@RestController
@RequestMapping("/rest/email/linux-mail")
class LinuxMailController(private val linuxMailService: LinuxMailService) {

    @PreAuthorize("hasAuthority('linux-mail')")
    @PostMapping(consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun acceptMail(
        @RequestHeader(HttpHeaders.CONTENT_LENGTH) contentLength: Long,
        request: HttpServletRequest
    ) {
        require(contentLength > 0) { "Empty Content" }
        require(contentLength <= MAX_MAIL_SIZE_BYTES) { "Mail too large: content-length $contentLength bytes exceeds limit of $MAX_MAIL_SIZE_BYTES bytes" }

        val address = AuthInfo.email ?: throw IllegalStateException("user has no email address")
        val data = saveStreamRead(request.inputStream, contentLength)
        val contents = String(data, Charsets.UTF_8).replace("\r\n", "\n")

        linuxMailService.forwardMail(address, contents)
    }

    private fun saveStreamRead(stream: InputStream, expectedSize: Long): ByteArray {
        require(expectedSize > 0) { "expected size must be positive" }
        require(expectedSize <= Int.MAX_VALUE) { "expected size must not exceed ${Int.MAX_VALUE}" }

        val buffer = ByteArray(expectedSize.toInt())

        val read = stream.readNBytes(buffer, 0, buffer.size)
        if (read != buffer.size) {
            throw IllegalStateException("Expected $expectedSize bytes, but got $read bytes")
        }

        // verify stream really done
        if (stream.read() != -1) {
            throw IllegalStateException("Stream larger than expected")
        }

        return buffer
    }

}
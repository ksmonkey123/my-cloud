package ch.awae.mycloud.module.documents

import ch.awae.mycloud.documents.DocumentData
import ch.awae.mycloud.documents.DocumentSource
import ch.awae.mycloud.test.mvc.ModuleWebTest
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import java.time.LocalDateTime

@WebMvcTest(DocumentsRestController::class)
class DocumentsRestControllerTest : ModuleWebTest() {

    @MockkBean
    private lateinit var service: DocumentDataStore

    @Test
    fun `link for missing document should return 404`() {
        every { service.retrieveById(any()) } returns null

        mvc.get("/documents/782c39ed-9b29-4b2e-9971-9cedf1ffc8b8")
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `illegal link should return 400`() {
        every { service.retrieveById(any()) } returns null

        mvc.get("/documents/illegal")
            .andExpect { status { isBadRequest() } }
    }

    @Test
    fun `valid document should return 200`() {
        every { service.retrieveById(any()) } returns DocumentData(
            source = DocumentSource.BOOKKEEPING,
            filename = "test.txt",
            type = MediaType.TEXT_PLAIN,
            validUntil = LocalDateTime.now().plusDays(1),
            content = "this is a test".toByteArray()
        )

        mvc.get("/documents/782c39ed-9b29-4b2e-9971-9cedf1ffc8b9")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.TEXT_PLAIN)
                    header { string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=test.txt") }
                    string("this is a test")
                }
            }
    }

}
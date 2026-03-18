package ch.awae.mycloud.module.shortener

import ch.awae.mycloud.test.mvc.ModuleWebTest
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.get
import java.util.*

@WebMvcTest(controllers = [LinkResolutionController::class])
class LinkResolutionControllerTest : ModuleWebTest() {

    @MockkBean
    lateinit var repo: ShortLinkRepository

    @Test
    fun `invalid link returns 404`() {
        every { repo.findById("invalid") } returns Optional.empty()

        mvc.get("/s/invalid")
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `valid link redirects`() {
        every { repo.findById("valid") } returns Optional.of(ShortLink("valid", "dummy", "http://example.com"))

        mvc.get("/s/valid")
            .andExpect {
                status { isFound() }
                header { string(HttpHeaders.LOCATION, "http://example.com") }
            }
    }

}
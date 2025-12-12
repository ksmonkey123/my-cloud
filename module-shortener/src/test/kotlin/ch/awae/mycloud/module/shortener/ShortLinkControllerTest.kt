package ch.awae.mycloud.module.shortener

import ch.awae.mycloud.test.WithBearerAuth
import ch.awae.mycloud.test.mvc.ModuleWebTest
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get

@WebMvcTest(controllers = [ShortLinkController::class])
class ShortLinkControllerTest : ModuleWebTest() {

    @MockkBean
    lateinit var shortLinkService: ShortLinkService

    @Test
    @WithBearerAuth(username = "user", authorities = ["shortener"])
    fun `list() should list links of user`() {
        every {
            shortLinkService.listShortLinks("user")
        } returns listOf(
            ShortLinkDTO("aaaa", "http://example.org/a"),
            ShortLinkDTO("bbbb", "http://example.org/b")
        )

        mvc.get("/rest/shortener/links").andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                json(
                    """
                    [
                    { "id": "aaaa", "targetUrl": "http://example.org/a" },
                    { "id": "bbbb", "targetUrl": "http://example.org/b" }
                    ]
                """.trimIndent()
                )
            }
        }

    }

    @Test
    @WithBearerAuth(username = "user", authorities = [])
    fun `list() without authority should not work`() {
        mvc.get("/rest/shortener/links").andExpect {
            status { isForbidden() }
        }
    }

}
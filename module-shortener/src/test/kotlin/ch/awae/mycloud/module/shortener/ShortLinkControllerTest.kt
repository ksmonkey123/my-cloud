package ch.awae.mycloud.module.shortener

import ch.awae.mycloud.test.*
import ch.awae.mycloud.test.mvc.*
import com.ninjasquad.springmockk.*
import io.mockk.*
import org.junit.jupiter.api.*
import org.springframework.boot.test.autoconfigure.web.servlet.*
import org.springframework.http.*
import org.springframework.test.web.servlet.*

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
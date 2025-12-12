package ch.awae.mycloud.module.auth.rest

import ch.awae.mycloud.api.auth.*
import ch.awae.mycloud.api.common.*
import ch.awae.mycloud.module.auth.domain.*
import ch.awae.mycloud.module.auth.service.*
import ch.awae.mycloud.test.*
import ch.awae.mycloud.test.mvc.*
import com.ninjasquad.springmockk.*
import io.mockk.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.*
import org.springframework.http.MediaType.*
import org.springframework.test.web.servlet.*
import java.time.*

@WebMvcTest(controllers = [SecurityController::class])
class SecurityControllerTest : ModuleWebTest() {

    @Autowired
    private lateinit var securityController: SecurityController

    @MockkBean
    private lateinit var securityService: SecurityService

    @BeforeEach
    fun setup() {
        clearMocks(securityService)
    }

    @Test
    @WithBearerAuth(
        username = "test-user",
        admin = false,
        email = "test.user@example.org",
        authorities = ["a", "b"],
        language = Language.ENGLISH,
        token = "dummy-token"
    )
    fun testAuthenticate() {
        mvc.get("/rest/auth/authenticate")
            .andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_JSON)
                    json(
                        """
                        {
                          "type": "USER",
                          "username": "test-user",
                          "email": "test.user@example.org",
                          "authorities": [
                            "user",
                            "a",
                            "b"
                          ],
                          "token": "dummy-token",
                          "languageCode": "en"
                        }
                    """.trimIndent()
                    )
                }
            }

        confirmVerified(securityService)
    }

    @Test
    @WithBearerAuth(token = "my-own-dummy-token")
    fun testLogout() {
        every { securityService.logout("my-own-dummy-token") } just runs

        mvc.post("/rest/auth/logout").andExpect { status { isNoContent() } }

        verify { securityService.logout("my-own-dummy-token") }

        confirmVerified(securityService)
    }

    @Test
    fun testLoginShort() {

        val account = mockk<Account>()
        val tokenGenerator = mockk<TokenGenerator>()
        every { tokenGenerator.generate(any(), any(), any(), any()) } returns "token-response"

        every {
            securityService.login("test-user", "password", TokenRetentionPolicy.SHORT)
        } returns AuthToken.buildToken(account, tokenGenerator, LocalDateTime.now().plusHours(1))

        mvc.post("/rest/auth/login") {
            contentType = APPLICATION_JSON
            content = """{"username": "test-user","password": "password", "longRetention":  false}"""
        }.andExpect {
            status { isOk() }
            content {
                contentType(APPLICATION_JSON)
                json("""{"token": "token-response"}""")
            }
        }

    }

    @Test
    fun testLoginLong() {

        val account = mockk<Account>()
        val tokenGenerator = mockk<TokenGenerator>()
        every { tokenGenerator.generate(any(), any(), any(), any()) } returns "token-response"

        every {
            securityService.login("test-user", "password", TokenRetentionPolicy.LONG)
        } returns AuthToken.buildToken(account, tokenGenerator, LocalDateTime.now().plusHours(1))

        mvc.post("/rest/auth/login") {
            contentType = APPLICATION_JSON
            content = """{"username": "test-user","password": "password", "longRetention":  true}"""
        }.andExpect {
            status { isOk() }
            content {
                contentType(APPLICATION_JSON)
                json("""{"token": "token-response"}""")
            }
        }

    }
}
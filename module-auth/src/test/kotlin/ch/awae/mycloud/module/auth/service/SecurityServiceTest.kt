package ch.awae.mycloud.module.auth.service

import ch.awae.mycloud.common.TokenGenerator
import ch.awae.mycloud.module.auth.*
import ch.awae.mycloud.module.auth.domain.*
import ch.awae.mycloud.module.auth.exception.*
import io.mockk.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertNotNull
import org.springframework.test.context.jdbc.*

class SecurityServiceTest : AuthModuleTest() {

    @BeforeEach
    fun setup() {
        clearMocks(tokenGenerator)
    }

    @Test
    @Sql(scripts = ["/testdata/clear_schema.sql", "/testdata/2000_setup_admin_users.sql"])
    fun testLogin() {
        every {
            tokenGenerator.generate(32, TokenGenerator.EncoderType.BASIC, any(), any())
        } returns "tH/Q7WKAW1xb6yVnW1rovIG6nbj+GxFatuWrKJavLIU"

        val token = securityService.login("test-user-2000", "password", TokenRetentionPolicy.SHORT)

        assertEquals("tH/Q7WKAW1xb6yVnW1rovIG6nbj+GxFatuWrKJavLIU", token.tokenString)
        assertEquals("test-user-2000", token.account.username)

        // after login, token is valid
        assertNotNull(authenticationService.authenticateToken("Bearer tH/Q7WKAW1xb6yVnW1rovIG6nbj+GxFatuWrKJavLIU"))
    }

    @Test
    @Sql(scripts = ["/testdata/clear_schema.sql", "/testdata/2000_setup_admin_users.sql"])
    fun testLoginBadUser() {
        assertThrows<BadLoginException> {
            securityService.login("bad_user", "bad_password", TokenRetentionPolicy.SHORT)
        }
    }

    @Test
    @Sql(scripts = ["/testdata/clear_schema.sql", "/testdata/2000_setup_admin_users.sql"])
    fun testLoginBadPassword() {
        assertThrows<BadLoginException> {
            securityService.login("test-user-2000", "bad_password", TokenRetentionPolicy.SHORT)
        }
    }

    @Test
    @Sql(scripts = ["/testdata/clear_schema.sql", "/testdata/2000_setup_admin_users.sql"])
    fun testLoginDisabledUser() {
        assertThrows<BadLoginException> {
            securityService.login("test-user-2001", "password", TokenRetentionPolicy.SHORT)
        }
    }

    @Test
    @Sql(scripts = ["/testdata/clear_schema.sql", "/testdata/3000_setup_logged_in_normal_user.sql"])
    fun testLogout() {
        securityService.logout("yH/Q7WKAW1xb6yVnW1rovIG6nbj+GxEbcWWrKJavLIU")

        val count = entityManager.createQuery(
            "select count(t) from AuthToken t where t.tokenString = :tokenString",
            Long::class.java
        )
            .setParameter("tokenString", "yH/Q7WKAW1xb6yVnW1rovIG6nbj+GxEbcWWrKJavLIU")
            .singleResult
        assertEquals(0, count)
    }

    @Test
    @Sql(scripts = ["/testdata/clear_schema.sql", "/testdata/1000_setup_bearer_token_test.sql"])
    fun tokenCleanup() {
        securityService.cleanOldTokens()

        val tokens = authTokenRepository.findAll().map { it.tokenString }

        assertTrue("MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE" in tokens)
        assertTrue("ODEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE" !in tokens)
    }

}
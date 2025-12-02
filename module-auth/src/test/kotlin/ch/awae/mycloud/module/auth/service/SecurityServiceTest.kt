package ch.awae.mycloud.module.auth.service

import ch.awae.mycloud.module.auth.*
import ch.awae.mycloud.module.auth.domain.*
import ch.awae.mycloud.module.auth.exception.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import org.springframework.test.context.jdbc.*

class SecurityServiceTest : AuthModuleTest() {

    @Test
    fun testLogin() {
        val token = securityService.login("admin", "admin", TokenRetentionPolicy.SHORT)

        assertNotNull(token)
        assertEquals("admin", token.account.username)

        // after login, token is valid
        assertNotNull(authenticationService.authenticateToken("Bearer " + token.tokenString))
    }

    @Test
    fun testLoginBadUser() {
        assertThrows<BadLoginException> {
            securityService.login("bad_user", "bad_password", TokenRetentionPolicy.SHORT)
        }
    }

    @Test
    fun testLoginBadPassword() {
        assertThrows<BadLoginException> {
            securityService.login("admin", "bad_password", TokenRetentionPolicy.SHORT)
        }
    }

    @Test
    fun testLogout() {
        val token = securityService.login("admin", "admin", TokenRetentionPolicy.SHORT)
        securityService.logout(token.tokenString)

        // after logout, token is invalid
        assertNull(authenticationService.authenticateToken("Bearer " + token.tokenString))
    }

    @Test
    @Sql(scripts = ["/testdata/insert_auth_token.sql"])
    fun tokenCleanup() {
        assertEquals(2, authTokenRepository.count())

        securityService.cleanOldTokens()

        assertEquals(1, authTokenRepository.count())

        val tokens = authTokenRepository.findAll().map { it.tokenString }

        assertTrue("valid_token" in tokens)
        assertTrue("expired_token" !in tokens)
    }

}
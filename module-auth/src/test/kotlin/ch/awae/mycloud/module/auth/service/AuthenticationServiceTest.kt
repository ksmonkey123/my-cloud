package ch.awae.mycloud.module.auth.service

import ch.awae.mycloud.module.auth.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.jdbc.*
import kotlin.test.*

class AuthenticationServiceTest : AuthModuleTest() {

    @Test
    @Sql(scripts = ["/testdata/insert_auth_token.sql"])
    fun testAuthenticateValidToken() {
        val info = authenticationService.authenticateToken("Bearer valid_token")
        assertNotNull(info)
        assertEquals("admin", info.username)
    }

    @Test
    @Sql(scripts = ["/testdata/insert_auth_token.sql"])
    fun testAuthenticateInvalidToken() {
        val info = authenticationService.authenticateToken("expired_token")
        assertNull(info)
    }
}
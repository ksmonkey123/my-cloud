package ch.awae.mycloud.module.auth.service

import ch.awae.mycloud.module.auth.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.jdbc.*
import kotlin.test.*

class AuthenticationServiceTest : AuthModuleTest() {

    @Test
    @Sql(scripts = ["/testdata/clear_schema.sql", "/testdata/1000_setup_bearer_token_test.sql"])
    fun testAuthenticateValidToken() {
        val info = authenticationService.authenticateToken("Bearer MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE")
        assertNotNull(info)
        assertEquals("test-user-1000", info.username)
    }

    @Test
    @Sql(scripts = ["/testdata/clear_schema.sql", "/testdata/1000_setup_bearer_token_test.sql"])
    fun testAuthenticateInvalidToken() {
        val info = authenticationService.authenticateToken("Bearer OTEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE")
        assertNull(info)
    }

    @Test
    @Sql(scripts = ["/testdata/clear_schema.sql", "/testdata/1000_setup_bearer_token_test.sql"])
    fun testAuthenticateDisabledUser() {
        val info = authenticationService.authenticateToken("Bearer QTEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE")
        assertNull(info)
    }


}
package ch.awae.mycloud.auth.service

import ch.awae.mycloud.auth.AuthModuleTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

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

    @Test
    fun testAuthenticateUnsupportedTokenType() {
        val info = authenticationService.authenticateToken("Unsupported Token")
        assertNull(info)
    }

    @Test
    @Sql(scripts = ["/testdata/clear_schema.sql", "/testdata/1000_setup_bearer_token_test.sql"])
    fun testAuthenticateApiKey() {
        val info =
            authenticationService.authenticateToken("Key -ER_dxeF4Pgj8EtA2iNQbpFUd305nLdwLF5GueWWuKJBf2oNErdjrCyXOg6gg6WA73GSY0Ai4qCxvKGs7EO7rw")
        assertNotNull(info)
    }

    @Test
    @Sql(scripts = ["/testdata/clear_schema.sql", "/testdata/1000_setup_bearer_token_test.sql"])
    fun testAuthenticateApiKeyDisabledUser() {
        val info =
            authenticationService.authenticateToken("Key -AR_dxeF4Pgj8EtA2iNQbpFUd305nLdwLF5GueWWuKJBf2oNErdjrCyXOg6gg6WA73GSY0Ai4qCxvKGs7EO7rw")
        assertNull(info)
    }

    fun testAuthenticateApiKeyInvalidToken() {
        val info =
            authenticationService.authenticateToken("Key -XR_dxeF4Pgj8EtA2iNQbpFUd305nLdwLF5GueWWuKJBf2oNErdjrCyXOg6gg6WA73GSY0Ai4qCxvKGs7EO7rw")
        assertNull(info)
    }

}
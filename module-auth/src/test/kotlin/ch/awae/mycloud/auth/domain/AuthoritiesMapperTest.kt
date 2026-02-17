package ch.awae.mycloud.auth.domain

import ch.awae.mycloud.auth.Language
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthoritiesMapperTest {

    @Test
    fun `getAuthorities(ApiKey) intersects`() {
        val apiKey = ApiKey(
            "test-key", "test-secret",
            Account(
                "test-user", "test-password",
                "test-email", true, false, Language.GERMAN,
                mutableSetOf(
                    Role("test-role", setOf("auth-1", "auth-2", "auth-3"), "test role")
                )
            ),
            setOf("auth-1", "auth-3", "auth-4")
        )

        val result = AuthoritiesMapper.getAuthorities(apiKey)

        assertEquals(setOf("auth-1", "auth-3", "api"), result)
    }

}
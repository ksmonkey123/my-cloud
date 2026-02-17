package ch.awae.mycloud.auth.config

import ch.awae.mycloud.auth.Language
import ch.awae.mycloud.auth.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.springframework.test.annotation.*
import org.springframework.test.context.jdbc.*
import kotlin.test.*

@DirtiesContext
class InitialUserCreatorTest : AuthModuleTest() {

    @Test
    @Sql(scripts = ["/testdata/clear_schema.sql"])
    fun `initial user must be created when no users exist`() {
        assertEquals(0, accountRepository.count())

        initialUserCreator.run()

        assertEquals(1, accountRepository.count())
        val admin = accountRepository.findByUsername("admin")
        assertNotNull(admin)
        assertNull(admin.email)
        assertTrue(passwordEncoder.matches("admin", admin.password))
        assertEquals(Language.ENGLISH, admin.language)
        assertTrue(admin.admin)
        assertTrue(admin.enabled)
    }

    @Test
    @Sql(scripts = ["/testdata/clear_schema.sql", "/testdata/3000_setup_logged_in_normal_user.sql"])
    fun `initial user must be created when no admins exist, even if normal user is present`() {
        assertEquals(1, accountRepository.count())

        initialUserCreator.run()

        assertEquals(2, accountRepository.count())
        assertNotNull(accountRepository.findByUsername("admin"))
    }


    @Test
    @Sql(scripts = ["/testdata/clear_schema.sql", "/testdata/2000_setup_admin_users.sql"])
    fun `initial user must not be created if an admin already exists`() {
        assertEquals(2, accountRepository.count())

        initialUserCreator.run()

        assertEquals(2, accountRepository.count())
        assertNull(accountRepository.findByUsername("admin"))
    }

}
package ch.awae.mycloud.module.auth.config

import ch.awae.mycloud.api.auth.*
import ch.awae.mycloud.module.auth.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.springframework.test.annotation.*
import org.springframework.test.context.jdbc.*
import kotlin.test.*

@DirtiesContext
class InitialUserCreatorTest : AuthModuleTest() {

    @Test
    @Sql(scripts = ["/testdata/clean_auth.sql"])
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
    @Sql(scripts = ["/testdata/clean_auth.sql", "/testdata/insert_normal_user.sql"])
    fun `initial user must be created when no admins exist, even if normal user is present`() {
        assertEquals(1, accountRepository.count())

        initialUserCreator.run()

        assertEquals(2, accountRepository.count())
        assertNotNull(accountRepository.findByUsername("admin"))
    }


    @Test
    @Sql(scripts = ["/testdata/clean_auth.sql", "/testdata/insert_admin_user.sql"])
    fun `initial user must not be created if an admin already exists`() {
        assertEquals(1, accountRepository.count())

        initialUserCreator.run()

        assertEquals(1, accountRepository.count())
        assertNull(accountRepository.findByUsername("admin"))
    }

}
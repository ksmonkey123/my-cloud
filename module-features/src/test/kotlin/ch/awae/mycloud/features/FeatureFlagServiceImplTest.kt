package ch.awae.mycloud.features

import ch.awae.mycloud.test.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.*
import org.springframework.data.repository.*
import org.springframework.test.context.*
import org.springframework.test.context.jdbc.*
import kotlin.test.*

class FeatureFlagServiceImplTest : ModuleTest() {

    @Autowired
    lateinit var service: FeatureFlagServiceImpl

    @Autowired
    lateinit var repo: FeatureFlagRepository

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testMissingFeatureFlag() {
        assertEquals(2, repo.count())
        assertFalse(service.isEnabled("test"))
        assertEquals(3, repo.count())
        val entity = repo.findByIdOrNull("test")
        assertNotNull(entity)
        assertFalse(entity.enabled)
    }

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testActiveFeatureFlag() {
        assertTrue(service.isEnabled("active"))
    }

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testInactiveFeatureFlag() {
        assertFalse(service.isEnabled("inactive"))
    }

}

@ActiveProfiles("default-true")
class FeatureFlagServiceImplTest_DefaultTrue : ModuleTest() {
    @Autowired
    lateinit var service: FeatureFlagServiceImpl

    @Autowired
    lateinit var repo: FeatureFlagRepository

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testMissingFeatureFlag() {
        assertEquals(2, repo.count())
        assertTrue(service.isEnabled("test"))
        assertEquals(3, repo.count())
        val entity = repo.findByIdOrNull("test")
        assertNotNull(entity)
        assertTrue(entity.enabled)
    }
}
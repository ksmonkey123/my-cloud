package ch.awae.mycloud.features

import org.junit.jupiter.api.Test
import org.springframework.data.repository.*
import org.springframework.test.context.*
import org.springframework.test.context.jdbc.*
import kotlin.test.*

class FeatureFlagServiceImplTest : FeaturesModuleTest() {

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testMissingFeatureFlag() {
        assertEquals(2, featureFlagRepository.count())
        assertFalse(featureFlagService.isEnabled("test"))
        assertEquals(3, featureFlagRepository.count())
        val entity = featureFlagRepository.findByIdOrNull("test")
        assertNotNull(entity)
        assertFalse(entity.enabled)
    }

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testActiveFeatureFlag() {
        assertTrue(featureFlagService.isEnabled("active"))
    }

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testInactiveFeatureFlag() {
        assertFalse(featureFlagService.isEnabled("inactive"))
    }

}

@ActiveProfiles("default-true")
class FeatureFlagServiceImplTest_DefaultTrue : FeaturesModuleTest() {


    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testMissingFeatureFlag() {
        assertEquals(2, featureFlagRepository.count())
        assertTrue(featureFlagService.isEnabled("test"))
        assertEquals(3, featureFlagRepository.count())
        val entity = featureFlagRepository.findByIdOrNull("test")
        assertNotNull(entity)
        assertTrue(entity.enabled)
    }
}
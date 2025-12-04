package ch.awae.mycloud.features.service

import ch.awae.mycloud.features.*
import jakarta.validation.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    @Test
    fun testInvalidFlag() {
        assertThrows<ConstraintViolationException> { featureFlagService.isEnabled("") }
        assertThrows<ConstraintViolationException> { featureFlagService.isEnabled("1234567890123456789012345678901") }
        assertThrows<ConstraintViolationException> { featureFlagService.isEnabled("this is a feature") }

        featureFlagService.isEnabled("1")
        featureFlagService.isEnabled("123456789012345678901234567890")
    }

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testFetchAll() {
        val result = featureFlagService.listAll(null)

        assertEquals(2, result.size)
        assertEquals("active", result[0].id)
        assertEquals(true, result[0].enabled)
        assertEquals("inactive", result[1].id)
        assertEquals(false, result[1].enabled)
    }

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testFetchActive() {
        val result = featureFlagService.listAll(true)

        assertEquals(1, result.size)
        assertEquals("active", result[0].id)
        assertEquals(true, result[0].enabled)
    }

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testFetchInactive() {
        val result = featureFlagService.listAll(false)

        assertEquals(1, result.size)
        assertEquals("inactive", result[0].id)
        assertEquals(false, result[0].enabled)
    }

}

@ActiveProfiles("default-true")
class FeatureFlagServiceImplDefaultTrueTest : FeaturesModuleTest() {


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
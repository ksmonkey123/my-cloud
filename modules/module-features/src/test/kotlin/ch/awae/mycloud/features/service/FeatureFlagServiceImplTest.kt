package ch.awae.mycloud.features.service

import ch.awae.mycloud.features.FeaturesModuleTest
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FeatureFlagServiceImplTest : FeaturesModuleTest() {

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun `reading missing flag creates it`() {
        // precondition
        assertEquals(2, featureFlagRepository.list().size)

        // act
        assertFalse(featureFlagService.isEnabled("test"))

        // assert
        val features = featureFlagRepository.list().toMap()
        assertEquals(3, features.size)
        assertEquals(false, features.get("test"))
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
        val result = featureFlagService.listAll(null).toMap()

        assertEquals(2, result.size)
        assertEquals(true, result.get("active"))
        assertEquals(false, result.get("inactive"))
    }

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testFetchActive() {
        val result = featureFlagService.listAll(true)

        assertEquals(1, result.size)
        assertEquals(Pair("active", true), result[0])
    }

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testFetchInactive() {
        val result = featureFlagService.listAll(false)

        assertEquals(1, result.size)
        assertEquals(Pair("inactive", false), result[0])
    }

}

@ActiveProfiles("default-true")
class FeatureFlagServiceImplDefaultTrueTest : FeaturesModuleTest() {

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun `reading missing flag creates it`() {
        // precondition
        assertEquals(2, featureFlagRepository.list().size)

        // act
        assertTrue(featureFlagService.isEnabled("test"))

        // assert
        val features = featureFlagRepository.list().toMap()
        assertEquals(3, features.size)
        assertEquals(true, features.get("test"))
    }
}
package ch.awae.mycloud.features

import ch.awae.mycloud.common.*
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.*
import kotlin.test.*

@ActiveProfiles("init-test")
class FeatureInitializerTest : FeaturesModuleTest() {

    @Test
    fun verifyFeatureInit() {
        featureFlagRepository.deleteAll()
        featureFlagInitializer.run()

        assertEquals(2, featureFlagRepository.count())
        assertFalse(featureFlagRepository.findByIdOrNull("feature1")!!.enabled)
        assertFalse(featureFlagRepository.findByIdOrNull("feature2")!!.enabled)
    }

    @Test
    fun `initializer should not override existing flags`() {
        featureFlagRepository.deleteAll()
        featureFlagRepository.save(FeatureFlag("feature1", true))

        featureFlagInitializer.run()

        assertEquals(2, featureFlagRepository.count())
        assertTrue(featureFlagRepository.findByIdOrNull("feature1")!!.enabled)
        assertFalse(featureFlagRepository.findByIdOrNull("feature2")!!.enabled)
    }

}

@ActiveProfiles("init-test", "default-true")
class FeatureInitializerDefaultTrueTest : FeaturesModuleTest() {

    @Test
    fun verifyFeatureInit() {
        featureFlagRepository.deleteAll()
        featureFlagInitializer.run()

        assertEquals(2, featureFlagRepository.count())
        assertTrue(featureFlagRepository.findByIdOrNull("feature1")!!.enabled)
        assertTrue(featureFlagRepository.findByIdOrNull("feature2")!!.enabled)
    }

}

@Profile("init-test")
@Configuration
class DummyModuleConfig() : ModuleConfiguration(
    name = "dummy",
    databaseSchemaName = null,
    features = listOf("feature1", "feature2")
)
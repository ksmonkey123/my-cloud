package ch.awae.mycloud.features.config

import ch.awae.mycloud.common.ModuleConfiguration
import ch.awae.mycloud.features.FeaturesModuleTest
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@ActiveProfiles("init-test")
class FeatureInitializerTest : FeaturesModuleTest() {

    @Test
    @Sql(statements = ["truncate features.feature_flag"])
    fun verifyFeatureInit() {
        featureFlagInitializer.run()

        val features = featureFlagRepository.list().toMap()
        assertEquals(2, features.size)
        assertEquals(false, features.get("feature1"))
        assertEquals(false, features.get("feature2"))
    }

    @Test
    @Sql(
        statements = [
            "truncate features.feature_flag",
            "insert into features.feature_flag (id, enabled) values ('feature1', true)"
        ]
    )
    fun `initializer should not override existing flags`() {
        featureFlagInitializer.run()

        val features = featureFlagRepository.list().toMap()
        assertEquals(2, features.size)
        assertEquals(true, features.get("feature1"))
        assertEquals(false, features.get("feature2"))
    }

}

@ActiveProfiles("init-test", "default-true")
class FeatureInitializerDefaultTrueTest : FeaturesModuleTest() {

    @Test
    @Sql(statements = ["truncate features.feature_flag"])
    fun verifyFeatureInit() {
        featureFlagInitializer.run()


        val features = featureFlagRepository.list().toMap()
        assertEquals(2, features.size)
        assertEquals(true, features.get("feature1"))
        assertEquals(true, features.get("feature2"))
    }

}

@Profile("init-test")
@Configuration
class DummyModuleConfig : ModuleConfiguration(
    name = "dummy",
    databaseSchemaName = null,
    features = listOf("feature1", "feature2")
)
package ch.awae.mycloud.features

import ch.awae.mycloud.api.features.*
import ch.awae.mycloud.test.*
import org.springframework.beans.factory.annotation.*

abstract class FeaturesModuleTest : ModuleTest() {

    @Autowired
    lateinit var featureFlagRepository: FeatureFlagRepository

    @Autowired
    lateinit var featureFlagService: FeatureFlagService

    @Autowired
    lateinit var featureFlagInitializer: FeatureInitializer


}
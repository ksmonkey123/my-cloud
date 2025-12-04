package ch.awae.mycloud.features

import ch.awae.mycloud.api.features.*
import ch.awae.mycloud.features.config.FeatureInitializer
import ch.awae.mycloud.features.model.FeatureFlagRepository
import ch.awae.mycloud.features.service.FeatureFlagServiceImpl
import ch.awae.mycloud.test.*
import org.springframework.beans.factory.annotation.*

abstract class FeaturesModuleTest : ModuleTest() {

    @Autowired
    lateinit var featureFlagRepository: FeatureFlagRepository

    @Autowired
    lateinit var featureFlagService: FeatureFlagServiceImpl

    @Autowired
    lateinit var featureFlagInitializer: FeatureInitializer


}
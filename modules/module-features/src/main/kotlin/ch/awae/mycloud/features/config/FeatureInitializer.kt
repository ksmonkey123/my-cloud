package ch.awae.mycloud.features.config

import ch.awae.mycloud.common.*
import ch.awae.mycloud.common.util.createLogger
import ch.awae.mycloud.features.model.FeatureFlag
import ch.awae.mycloud.features.model.FeatureFlagRepository
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.*
import org.springframework.stereotype.*

@Component
class FeatureInitializer(
    private val modules: List<ModuleConfiguration>,
    private val repo: FeatureFlagRepository,
    @param:Value("\${features.default-state}")
    private val initialFeatureState: Boolean,
) : CommandLineRunner {

    private val log = createLogger()

    override fun run(vararg args: String) {
        for (module in modules) {
            for (feature in module.features) {
                if (!repo.existsById(feature)) {
                    repo.save(FeatureFlag(feature, initialFeatureState))
                    log.info("created new feature {}", feature)
                }
            }
        }
        log.info("feature flags initialized")
    }

}
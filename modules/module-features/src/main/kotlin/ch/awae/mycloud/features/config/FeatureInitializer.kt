package ch.awae.mycloud.features.config

import ch.awae.mycloud.common.ModuleConfiguration
import ch.awae.mycloud.common.util.createLogger
import ch.awae.mycloud.features.model.FeatureFlagRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

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
                    repo.setState(feature, initialFeatureState)
                    log.info("created new feature {}", feature)
                }
            }
        }
        log.info("feature flags initialized")
    }

}
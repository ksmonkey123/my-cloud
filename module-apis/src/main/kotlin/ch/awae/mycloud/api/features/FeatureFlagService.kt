package ch.awae.mycloud.api.features

import jakarta.validation.constraints.*
import org.hibernate.validator.constraints.*

/**
 * Service that allows the querying of feature flags.
 */
interface FeatureFlagService {

    /**
     * Tests if a given feature flag is active or not.
     *
     * Missing feature flags will automatically be created. The initial value of newly created feature flags depends
     * on the boolean config parameter `features.default-state`
     *
     * @param feature the feature flag ID to check
     */
    fun isEnabled(@Length(min = 1, max = 30) @Pattern(regexp = "[A-Za-z0-9._:-]{1,30}") feature: String): Boolean

}

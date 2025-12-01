package ch.awae.mycloud.api.features

interface FeatureFlagService {

    fun isEnabled(feature: String): Boolean

    fun isEnabled(feature: FeatureFlag): Boolean {
        return isEnabled(feature.feature)
    }

}

interface FeatureFlag {
    val feature: String
}
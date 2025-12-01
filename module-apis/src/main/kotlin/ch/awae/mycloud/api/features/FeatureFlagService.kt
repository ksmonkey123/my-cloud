package ch.awae.mycloud.api.features

interface FeatureFlagService {

    fun isEnabled(feature: String): Boolean

}

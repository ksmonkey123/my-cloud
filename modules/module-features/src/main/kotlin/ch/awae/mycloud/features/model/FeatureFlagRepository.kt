package ch.awae.mycloud.features.model

interface FeatureFlagRepository {

    fun existsById(id: String): Boolean

    fun getState(feature: String): Boolean?

    fun setState(feature: String, state: Boolean)

    fun delete(feature: String)

    fun list(state: Boolean? = null): List<Pair<String, Boolean>>
}
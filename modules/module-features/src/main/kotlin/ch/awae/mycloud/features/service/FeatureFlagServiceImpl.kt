package ch.awae.mycloud.features.service

import ch.awae.mycloud.features.FeatureFlagService
import ch.awae.mycloud.features.model.FeatureFlagRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Service
@Transactional
@Validated
class FeatureFlagServiceImpl(
    private val repo: FeatureFlagRepository,
    @param:Value("\${features.default-state}") private val enabledByDefault: Boolean,
) : FeatureFlagService {

    override fun isEnabled(feature: String): Boolean {
        val state = repo.getState(feature)
        if (state != null) {
            return state
        }

        // create flag
        repo.setState(feature, enabledByDefault)
        return enabledByDefault
    }

    fun listAll(state: Boolean?): List<Pair<String, Boolean>> {
        return repo.list(state)
    }

    fun update(feature: String, enabled: Boolean) {
        repo.setState(feature, enabled)
    }

    fun delete(feature: String) {
        repo.delete(feature)
    }

}
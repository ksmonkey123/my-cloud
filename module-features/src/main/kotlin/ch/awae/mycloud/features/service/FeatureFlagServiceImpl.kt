package ch.awae.mycloud.features.service

import ch.awae.mycloud.api.features.FeatureFlagService
import ch.awae.mycloud.features.model.FeatureFlag
import ch.awae.mycloud.features.model.FeatureFlagRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
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
        val flag = repo.findByIdOrNull(feature)
        if (flag != null) {
            return flag.enabled
        }

        // create flag
        repo.save(FeatureFlag(feature, enabledByDefault))
        return enabledByDefault
    }

    fun listAll(state: Boolean?): List<FeatureFlag> {
        return if (state == null) {
            repo.listAllSorted()
        } else {
            repo.listByEnabled(state)
        }
    }

}
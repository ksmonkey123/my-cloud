package ch.awae.mycloud.features

import ch.awae.mycloud.api.features.*
import jakarta.transaction.*
import org.springframework.beans.factory.annotation.*
import org.springframework.data.repository.*
import org.springframework.stereotype.*
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

}
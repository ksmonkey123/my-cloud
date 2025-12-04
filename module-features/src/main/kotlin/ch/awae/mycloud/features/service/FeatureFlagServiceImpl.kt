package ch.awae.mycloud.features.service

import ch.awae.mycloud.api.features.*
import ch.awae.mycloud.common.*
import ch.awae.mycloud.features.model.*
import jakarta.transaction.*
import org.springframework.beans.factory.annotation.*
import org.springframework.data.repository.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*

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

    fun update(id: String, enabled: Boolean): FeatureFlag {
        val flag = repo.findByIdOrNull(id) ?: throw ResourceNotFoundException("/features/$id")
        flag.enabled = enabled
        return repo.save(flag)
    }

}
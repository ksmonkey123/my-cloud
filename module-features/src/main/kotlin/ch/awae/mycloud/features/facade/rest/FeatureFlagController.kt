package ch.awae.mycloud.features.facade.rest

import ch.awae.mycloud.features.model.*
import ch.awae.mycloud.features.service.*
import org.springframework.beans.factory.annotation.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/features")
class FeatureFlagController(
    @param:Value("\${features.default-state}") private val enabledByDefault: Boolean,
    private val service: FeatureFlagServiceImpl,
) {

    @GetMapping
    fun listFeatures(@RequestParam(required = false) active: Boolean?): FeatureListTO {
        return FeatureListTO(
            enabledByDefault,
            service.listAll(active).map {
                FeatureTO(it.id, it.enabled)
            }
        )
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/{id}")
    fun updateFeature(@PathVariable id: String, @RequestBody request: FeatureUpdateRequest): FeatureTO {
        val updated = service.update(id, request.enabled)
        return FeatureTO(updated)
    }

    data class FeatureUpdateRequest(val enabled: Boolean)

    data class FeatureListTO(val defaultState: Boolean, val features: List<FeatureTO>)
    data class FeatureTO(val id: String, val enabled: Boolean) {
        constructor(feature: FeatureFlag) : this(feature.id, feature.enabled)
    }

}
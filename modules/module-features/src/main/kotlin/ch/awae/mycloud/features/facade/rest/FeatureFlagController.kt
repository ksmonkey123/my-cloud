package ch.awae.mycloud.features.facade.rest

import ch.awae.mycloud.features.service.FeatureFlagServiceImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
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
            service.listAll(active).map { (id, enabled) ->
                FeatureTO(id, enabled)
            }
        )
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/{id}")
    fun editFeature(@PathVariable id: String, @RequestBody request: FeatureUpdateRequest): FeatureTO {
        service.update(id, request.enabled)
        return FeatureTO(id, request.enabled)
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFeature(@PathVariable id: String) {
        service.delete(id)
    }

    data class FeatureUpdateRequest(val enabled: Boolean)
    data class FeatureListTO(val defaultState: Boolean, val features: List<FeatureTO>)
    data class FeatureTO(val id: String, val enabled: Boolean)

}
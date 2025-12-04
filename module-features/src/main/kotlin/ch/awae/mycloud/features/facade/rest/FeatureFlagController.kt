package ch.awae.mycloud.features.facade.rest

import ch.awae.mycloud.common.*
import ch.awae.mycloud.features.model.*
import ch.awae.mycloud.features.service.*
import org.springframework.beans.factory.annotation.*
import org.springframework.http.*
import org.springframework.security.access.prepost.*
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
    fun editFeature(@PathVariable id: String, @RequestBody request: FeatureUpdateRequest): ResponseEntity<FeatureTO> {
        val result = service.update(id, request.enabled)

        val response = FeatureTO(result.value)

        return when (result) {
            is UpdateResult.Created -> ResponseEntity(response, HttpStatus.CREATED)
            is UpdateResult.Updated -> ResponseEntity(response, HttpStatus.OK)
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFeature(@PathVariable id: String) {
        service.delete(id)
    }

    data class FeatureUpdateRequest(val enabled: Boolean)
    data class FeatureListTO(val defaultState: Boolean, val features: List<FeatureTO>)
    data class FeatureTO(val id: String, val enabled: Boolean) {
        constructor(feature: FeatureFlag) : this(feature.id, feature.enabled)
    }

}
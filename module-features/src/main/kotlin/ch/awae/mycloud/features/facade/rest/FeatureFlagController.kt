package ch.awae.mycloud.features.facade.rest

import ch.awae.mycloud.features.service.FeatureFlagServiceImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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

    data class FeatureListTO(val defaultState: Boolean, val features: List<FeatureTO>)
    data class FeatureTO(val id: String, val enabled: Boolean)

}
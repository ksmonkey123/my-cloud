package ch.awae.mycloud.features.facade.rest

import ch.awae.mycloud.features.model.FeatureFlag
import ch.awae.mycloud.features.service.FeatureFlagServiceImpl
import ch.awae.mycloud.test.mvc.*
import com.ninjasquad.springmockk.*
import io.mockk.*
import org.junit.jupiter.api.*
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.*
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.json.*
import org.springframework.test.web.servlet.*

@WebMvcTest(FeatureFlagController::class)
class FeatureFlagControllerTest : ModuleWebTest() {

    @MockkBean
    private lateinit var featureFlagService: FeatureFlagServiceImpl

    @Test
    fun `returns full list`() {
        every { featureFlagService.listAll(null) } returns listOf(
            FeatureFlag("feature1", true),
            FeatureFlag("feature2", false)
        )

        mvc.get("/rest/features")
            .andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_JSON)
                    json(
                        """
                        {
                          "defaultState": false,
                          "features": [
                            {
                              "id": "feature1",
                              "enabled": true
                            },
                            {
                              "id": "feature2",
                              "enabled": false
                            }
                          ]
                        }""".trimIndent(), JsonCompareMode.STRICT
                    )
                }
            }
    }

    @Test
    fun `returns list of actives`() {
        every { featureFlagService.listAll(true) } returns listOf(
            FeatureFlag("feature1", true),
            FeatureFlag("feature2", true)
        )

        mvc.get("/rest/features?active=true")
            .andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_JSON)
                    json(
                        """
                        {
                          "defaultState": false,
                          "features": [
                            {
                              "id": "feature1",
                              "enabled": true
                            },
                            {
                              "id": "feature2",
                              "enabled": true
                            }
                          ]
                        }""".trimIndent(), JsonCompareMode.STRICT
                    )
                }

            }
    }

    @Test
    fun `returns list of inactives`() {
        every { featureFlagService.listAll(false) } returns listOf(
            FeatureFlag("feature1", false),
            FeatureFlag("feature2", false)
        )

        mvc.get("/rest/features?active=false")
            .andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_JSON)
                    json(
                        """
                        {
                          "defaultState": false,
                          "features": [
                            {
                              "id": "feature1",
                              "enabled": false
                            },
                            {
                              "id": "feature2",
                              "enabled": false
                            }
                          ]
                        }""".trimIndent(), JsonCompareMode.STRICT
                    )
                }
            }
    }

}

@ActiveProfiles("default-true")
@WebMvcTest(FeatureFlagController::class)
class FeatureFlagControllerDefaultTrueTest : ModuleWebTest() {

    @MockkBean
    private lateinit var featureFlagService: FeatureFlagServiceImpl

    @Test
    fun `returns full list`() {
        every { featureFlagService.listAll(null) } returns listOf(
            FeatureFlag("feature1", true),
            FeatureFlag("feature2", false)
        )

        mvc.get("/rest/features")
            .andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_JSON)
                    json(
                        """
                        {
                          "defaultState": true,
                          "features": [
                            {
                              "id": "feature1",
                              "enabled": true
                            },
                            {
                              "id": "feature2",
                              "enabled": false
                            }
                          ]
                        }""".trimIndent(), JsonCompareMode.STRICT
                    )
                }
            }
    }
}



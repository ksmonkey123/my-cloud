package ch.awae.mycloud.service.canary.web.dto

import ch.awae.mycloud.service.canary.web.model.*
import jakarta.validation.*
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.*
import java.time.*

data class MonitoredSiteDto(
    @field:URL
    val url: String,
    val enabled: Boolean,
    @field:Valid
    val tests: List<SiteTestDto>,
)

data class SiteTestDto(
    @field:NotBlank
    @field:Length(min = 1)
    val testString: String,
    val enabled: Boolean,
)

data class MonitoredSiteSummaryDto(
    val id: Long,
    val url: String,
    val enabled: Boolean,
    val tests: List<SiteTestDto>,
    val latestTest: TestRecordDto?,
)

data class TestRecordDto(
    val result: TestResult,
    val timestamp: LocalDateTime,
)
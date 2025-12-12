package ch.awae.mycloud.ytdl

import ch.awae.mycloud.api.auth.AuthInfo
import ch.awae.mycloud.common.PageDto
import ch.awae.mycloud.ytdl.dto.JobDto
import jakarta.validation.Valid
import org.hibernate.validator.constraints.URL
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/ytdl/jobs")
@PreAuthorize("hasAuthority('ytdl')")
class JobRestController(
    val service: JobService,
) {

    @GetMapping
    fun list(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") pageSize: Int,
    ): PageDto<JobDto> {
        return service.listJobs(AuthInfo.username, page, pageSize)
    }

    @PostMapping
    fun createJob(@Valid @RequestBody request: JobCreationRequest) {
        service.createJob(AuthInfo.username, request.url, request.format)
    }

}

data class JobCreationRequest(
    @param:URL
    val url: String,
    val format: OutputFormat,
)
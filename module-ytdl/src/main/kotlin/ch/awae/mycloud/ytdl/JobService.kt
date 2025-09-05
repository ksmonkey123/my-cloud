package ch.awae.mycloud.ytdl

import ch.awae.mycloud.common.PageDto
import ch.awae.mycloud.ytdl.dto.JobDto
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.hibernate.validator.constraints.URL
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Transactional
@Service
class JobService(val repo: JobRepository) {

    fun createJob(owner: String, @Valid @URL url: String, format: OutputFormat): Job {
        val job = Job(owner, url, format)
        return repo.save(job)
    }

    fun listJobs(owner: String, pageNumber: Int, pageSize: Int): PageDto<JobDto> {
        val page = repo.findByOwner(owner, PageRequest.of(pageNumber, pageSize))
        return PageDto(
            page.content.map { JobDto(it) },
            page.totalElements,
        )
    }


}
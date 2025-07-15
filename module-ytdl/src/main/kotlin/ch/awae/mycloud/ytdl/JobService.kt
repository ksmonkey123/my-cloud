package ch.awae.mycloud.ytdl

import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.hibernate.validator.constraints.URL
import org.springframework.stereotype.Service

@Transactional
@Service
class JobService(val repo: JobRepository) {

    fun createJob(owner: String, @Valid @URL url: String, format: OutputFormat): Job {
        val job = Job(owner, url, format)
        return repo.save(job)
    }

}
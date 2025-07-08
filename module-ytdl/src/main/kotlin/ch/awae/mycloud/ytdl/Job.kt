package ch.awae.mycloud.ytdl

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import java.util.*

@Entity(name = "YTDL_Job")
class Job(
    @Column(updatable = false) val username: String,
    @Column(updatable = false) val url: String,
    @Column(updatable = false) @Enumerated(EnumType.STRING) val format: OutputFormat,
    @Column(updatable = false, columnDefinition = "uuid") val uuid: UUID = UUID.randomUUID(),
    @Enumerated(EnumType.STRING) val status: JobStatus = JobStatus.PENDING,
) : IdBaseEntity()

interface JobRepository : JpaRepository<Job, Long>

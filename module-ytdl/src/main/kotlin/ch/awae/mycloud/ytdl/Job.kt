package ch.awae.mycloud.ytdl

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import org.springframework.data.domain.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query
import java.time.*
import java.util.*

@Entity(name = "YTDL_Job")
@Table(name = "job", schema = "ytdl")
class Job(
    @Column(updatable = false) val owner: String,
    @Column(updatable = false) val url: String,
    @Column(updatable = false) @Enumerated(EnumType.STRING) val format: OutputFormat,
    @Column(updatable = false, columnDefinition = "uuid") val uuid: UUID = UUID.randomUUID(),
    @Column(updatable = false) val createdAt: LocalDateTime = LocalDateTime.now(),
) : IdBaseEntity() {

    @Enumerated(EnumType.STRING)
    var status: JobStatus = JobStatus.PENDING
        private set

    var submittedAt: LocalDateTime? = null
        private set

    var startedAt: LocalDateTime? = null
        private set

    var completedAt: LocalDateTime? = null
        private set

    @ElementCollection
    @CollectionTable(schema = "ytdl", name = "job_files", joinColumns = [JoinColumn(name = "job_id")])
    @MapKeyColumn(name = "file_name")
    @Column(name = "file_size")
    private val _files: MutableMap<String, Long> = mutableMapOf()

    val files: Map<String, Long>
        get() = _files

    fun markAsSubmitted() {
        status = JobStatus.SUBMITTED
        submittedAt = LocalDateTime.now()
    }

    fun markAsRunning() {
        status = JobStatus.RUNNING
        startedAt = LocalDateTime.now()
    }

    fun markAsCompleted() {
        status = JobStatus.COMPLETED
        completedAt = LocalDateTime.now()

        /*
         * it is possible to "miss" the start of the processing. In such a case we don't have a 'startedAt' timestamp.
         * We simply set the completion timestamp as the start in these cases for data consistency.
         */
        if (startedAt == null) {
            startedAt = completedAt
        }
    }

}

interface JobRepository : JpaRepository<Job, Long> {

    @Query("select j from YTDL_Job j where j.owner = :owner order by j.createdAt desc")
    fun findByOwner(owner: String, pageable: Pageable): Page<Job>

    @Query("select j from YTDL_Job j where j.status = ch.awae.mycloud.ytdl.JobStatus.PENDING order by j.createdAt asc limit :limit")
    fun findPending(limit: Int): List<Job>

    @Query("select j from YTDL_Job j where j.submittedAt is not null and j.completedAt is null")
    fun findRunning(): List<Job>

}
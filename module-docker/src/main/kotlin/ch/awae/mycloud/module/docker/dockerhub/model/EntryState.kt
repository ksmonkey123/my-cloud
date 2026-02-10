package ch.awae.mycloud.module.docker.dockerhub.model

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import org.springframework.data.domain.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query
import java.time.*

@Entity(name = "docker_EntryState")
@Table(name = "docker_entry_state", schema = "docker")
class EntryState(
    @ManyToOne
    @JoinColumn(updatable = false, nullable = false)
    val monitoredEntry: MonitoredEntry,
    @Column(updatable = false, nullable = false)
    val digest: String,
    @Convert(converter = TagListConverter::class)
    @Column(updatable = false, nullable = false)
    val tags: List<String>,
    @Column(updatable = false, nullable = false)
    val recordedAt: LocalDateTime = LocalDateTime.now(),
) : IdBaseEntity()

interface EntryStateRepository : JpaRepository<EntryState, Long> {

    @Query("select es from docker_EntryState es where es.monitoredEntry = :monitoredEntry order by es.recordedAt desc")
    fun findByMonitoredEntry(monitoredEntry: MonitoredEntry, pageable: Pageable): List<EntryState>

}
package ch.awae.mycloud.module.canary.dockerhub.model

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import org.springframework.data.domain.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query

@Entity
@Table(name = "docker_entry_state", schema = "canary")
class EntryState(
    @ManyToOne
    val monitoredEntry: MonitoredEntry,
    val digest: String,
    @Convert(converter = TagListConverter::class)
    val tags: List<String>,
) : IdBaseEntity()

interface EntryStateRepository : JpaRepository<EntryState, Long> {

    @Query("select es from EntryState es where es.monitoredEntry = :monitoredEntry order by es._creationTimestamp desc")
    fun findByMonitoredEntry(monitoredEntry: MonitoredEntry, pageable: Pageable): List<EntryState>

}
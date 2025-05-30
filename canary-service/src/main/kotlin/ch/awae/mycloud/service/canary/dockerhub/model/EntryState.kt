package ch.awae.mycloud.service.canary.dockerhub.model

import ch.awae.mycloud.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*

@Entity
@Table(name = "docker_entry_state")
class EntryState(
    @ManyToOne
    val monitoredEntry: MonitoredEntry,
    val digest: String,
    @Convert(converter = TagListConverter::class)
    val tags: List<String>,
) : IdBaseEntity()

interface EntryStateRepository : JpaRepository<EntryState, Long>

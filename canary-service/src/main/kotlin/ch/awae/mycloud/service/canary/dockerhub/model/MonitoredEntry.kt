package ch.awae.mycloud.service.canary.dockerhub.model

import ch.awae.mycloud.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query

@Entity
@Table(name = "docker_monitored_entry")
class MonitoredEntry(
    val namespace: String?,
    val repository: String,
    val tag: String,
    val tagChangesOnly: Boolean,
    val enabled: Boolean,
) : IdBaseEntity() {

    @OneToOne(mappedBy = "monitoredEntry")
    val currentState: CurrentState? = null

    val descriptor: String
        get() = "${namespace ?: "_"}/$repository:$tag"

    val webIdentifier: String
        get() = if (namespace != null) "r/$namespace/$repository" else "_/$repository"

}

interface MonitoredEntryRepository : JpaRepository<MonitoredEntry, Long> {

    @Query("select me.id from MonitoredEntry me where me.enabled")
    fun listIdsOfEnabled(): List<Long>

    @Query("select me from MonitoredEntry me where me.namespace = :namespace and me.repository = :repository and me.tag = :tag")
    fun findByIdentifier(namespace: String?, repository: String, tag: String): MonitoredEntry?

}

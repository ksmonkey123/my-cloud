package ch.awae.mycloud.module.docker.dockerhub.model

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query

@Entity(name = "docker_MonitoredEntry")
@Table(name = "docker_monitored_entry", schema = "docker")
class MonitoredEntry(
    @Column(updatable = false, nullable = false)
    val owner: String,
    @Column(updatable = false)
    val namespace: String?,
    @Column(updatable = false, nullable = false)
    val repository: String,
    @Column(updatable = false, nullable = false)
    val tag: String,
    @Column(updatable = false, nullable = false)
    val tagChangesOnly: Boolean,
    @Column(updatable = false, nullable = false)
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

    @Query("select me.id from docker_MonitoredEntry me where me.enabled")
    fun listIdsOfEnabled(): List<Long>

    @Query("select me from docker_MonitoredEntry me where me.namespace = :namespace and me.repository = :repository and me.tag = :tag")
    fun findByIdentifier(namespace: String?, repository: String, tag: String): MonitoredEntry?

}

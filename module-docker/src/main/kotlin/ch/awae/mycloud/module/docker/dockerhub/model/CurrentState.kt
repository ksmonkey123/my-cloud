package ch.awae.mycloud.module.docker.dockerhub.model

import ch.awae.mycloud.common.util.equalByField
import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import java.time.*

@Entity(name = "docker_CurrentState")
@Table(name = "v_docker_current_state", schema = "docker")
@Immutable
class CurrentState(
    @Id
    @OneToOne
    val monitoredEntry: MonitoredEntry,
    @Column(updatable = false, nullable = false)
    val digest: String,
    @Convert(converter = TagListConverter::class)
    @Column(updatable = false, nullable = false)
    val tags: List<String>,
    @Column(updatable = false, nullable = false)
    val recordedAt: LocalDateTime,
) {
    override fun equals(other: Any?): Boolean = equalByField(other, CurrentState::monitoredEntry)
    override fun hashCode(): Int = monitoredEntry.hashCode()
}
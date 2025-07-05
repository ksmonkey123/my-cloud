package ch.awae.mycloud.module.docker.dockerhub.model

import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import java.time.*

@Entity
@Table(name = "v_docker_current_state", schema = "docker")
@Immutable
data class CurrentState(
    @Id
    @OneToOne
    val monitoredEntry: MonitoredEntry,
    val digest: String,
    @Convert(converter = TagListConverter::class)
    val tags: List<String>,
    val recordedAt: LocalDateTime,
)
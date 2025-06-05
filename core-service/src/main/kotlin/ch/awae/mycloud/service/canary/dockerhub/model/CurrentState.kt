package ch.awae.mycloud.service.canary.dockerhub.model

import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import java.time.*

@Entity
@Table(name = "v_canary_docker_current_state")
@Immutable
class CurrentState(
    @Id
    @OneToOne
    val monitoredEntry: MonitoredEntry,
    val digest: String,
    @Convert(converter = TagListConverter::class)
    val tags: List<String>,
    val recordedAt: LocalDateTime,
)
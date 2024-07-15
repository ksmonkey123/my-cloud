package ch.awae.mycloud.service.audit.model

import ch.awae.mycloud.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import java.time.*

@Entity
class LogEntry(
    val timing: Timing,
    val traceId: String,
    val serviceName: String,
    val account: String?,
    val request: Request?,
    val method: Method,
) : IdBaseEntity() {
    val traceRoot = traceId.split('$').first()
}

@Embeddable
class Timing(
    @Column(name = "timestamp_start")
    val start: LocalDateTime,
    @Column(name = "timestamp_end")
    val end: LocalDateTime,
)

@Embeddable
class Request(
    val verb: String,
    val path: String,
)

@Embeddable
class Method(
    val component: String,
    val method: String,
    val error: String?,
) {
    @ElementCollection
    @CollectionTable(
        name = "method_parameter",
        joinColumns = [JoinColumn(name = "log_entry_id", referencedColumnName = "id")]
    )
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    val parameter: MutableMap<String, String?> = mutableMapOf()
}

interface LogEntryRepository : JpaRepository<LogEntry, Long> {

    fun existsByTraceId(traceId: String): Boolean

}
package ch.awae.mycloud.service.audit.service

import ch.awae.mycloud.*
import ch.awae.mycloud.audit.*
import ch.awae.mycloud.service.audit.model.*
import jakarta.transaction.*
import org.springframework.stereotype.*

@Transactional
@Service
class AuditService(
    private val logEntryRepository: LogEntryRepository,
) {

    private val logger = createLogger()

    fun write(entry: AuditLogEntry) {
        logger.info("handling traceId='${entry.traceId}' for ${entry.service}:${entry.method.component}#${entry.method.method}")
        val logEntry = LogEntry(
            Timing(
                entry.timing.start.toLocalDateTime(),
                entry.timing.end.toLocalDateTime(),
            ),
            entry.traceId,
            entry.service,
            entry.username,
            entry.request?.let {
                Request(
                    it.verb,
                    it.path,
                )
            },
            entry.method.let {
                Method(
                    it.component,
                    it.method,
                    it.error,
                )
            },
        )

        logEntry.method.parameter.putAll(entry.method.parameters)

        // assume uniqueness - potential duplicates
        logEntryRepository.save(logEntry)
    }

}
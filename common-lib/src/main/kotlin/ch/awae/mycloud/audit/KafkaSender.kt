package ch.awae.mycloud.audit

import org.springframework.kafka.core.*
import org.springframework.stereotype.*
import java.util.concurrent.*

@Component
class KafkaSender(private val kafkaTemplate: KafkaTemplate<String, Any>) {

    private val executor = Executors.newSingleThreadExecutor()

    fun send(auditLogEntry: AuditLogEntry) {
        executor.submit {
            kafkaTemplate.send("mycloud-audit-log", auditLogEntry)
        }
    }

}
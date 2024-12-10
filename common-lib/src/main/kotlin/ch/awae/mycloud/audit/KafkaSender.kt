package ch.awae.mycloud.audit

import org.springframework.kafka.core.*
import org.springframework.stereotype.*
import java.util.concurrent.*

const val AUDIT_LOG_KAFKA_TOPIC = "mycloud.audit-log"

@Component
class KafkaSender(private val kafkaTemplate: KafkaTemplate<String, Any>) {

    private val executor = Executors.newSingleThreadExecutor()

    fun send(auditLogEntry: AuditLogEntry) {
        executor.submit {
            kafkaTemplate.send(AUDIT_LOG_KAFKA_TOPIC, auditLogEntry)
        }
    }

}
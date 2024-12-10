package ch.awae.mycloud.service.audit.kafka

import ch.awae.mycloud.audit.*
import ch.awae.mycloud.auth.*
import ch.awae.mycloud.service.audit.service.*
import org.springframework.kafka.annotation.*
import org.springframework.stereotype.*

@Component
// TODO: remove legacy topic
@KafkaListener(topics = [AUDIT_LOG_KAFKA_TOPIC], containerFactory = "kafkaListenerContainerFactory")
class KafkaConsumer(private val svc: AuditService) {

    @KafkaHandler
    fun handleAuditLogEntry(entry: AuditLogEntry) {
        AuthInfo.impersonate("audit-kafka") {
            svc.write(entry)
        }
    }

}
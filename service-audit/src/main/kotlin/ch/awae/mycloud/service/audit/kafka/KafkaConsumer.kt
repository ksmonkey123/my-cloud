package ch.awae.mycloud.service.audit.kafka

import ch.awae.mycloud.audit.*
import ch.awae.mycloud.auth.*
import ch.awae.mycloud.service.audit.service.*
import org.springframework.kafka.annotation.*
import org.springframework.stereotype.*

@Component
@KafkaListener(topics = ["\${mycloud.audit.kafka.topic}"], containerFactory = "auditKafkaListenerContainerFactory")
class KafkaConsumer(private val svc: AuditService) {

    @KafkaHandler
    fun handleAuditLogEntry(entry: AuditLogEntry) {
        AuthInfo.impersonate("audit-kafka") {
            svc.write(entry)
        }
    }

}
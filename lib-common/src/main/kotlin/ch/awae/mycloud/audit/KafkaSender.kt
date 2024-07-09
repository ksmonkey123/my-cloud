package ch.awae.mycloud.audit

import org.apache.kafka.clients.producer.*
import org.apache.kafka.common.serialization.*
import org.springframework.beans.factory.annotation.*
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.*
import org.springframework.stereotype.*
import java.util.concurrent.*

@Component
class KafkaSender(
    @Value("\${mycloud.kafka.url}") private val server: String,
    @Value("\${mycloud.audit.kafka.topic}") private val kafkaTopic: String,
) {

    private val kafkaTemplate = KafkaTemplate(
        DefaultKafkaProducerFactory<String, Any>(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to server,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
            )
        )
    )

    private val executor = Executors.newSingleThreadExecutor()

    fun send(auditLogEntry: AuditLogEntry) {
        executor.submit {
            kafkaTemplate.send(kafkaTopic, auditLogEntry)
        }
    }

}
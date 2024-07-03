package ch.awae.mycloud.audit

import org.apache.kafka.clients.producer.*
import org.apache.kafka.common.serialization.*
import org.springframework.beans.factory.annotation.*
import org.springframework.context.annotation.*
import org.springframework.kafka.annotation.*
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.*

@Configuration
@EnableKafka
@Import(AuditLogAspect::class, AuditLogService::class, KafkaSender::class)
class AuditLogConfiguration(@Value("\${mycloud.kafka.url}") val server: String,
) {

    @Bean
    fun jsonKafkaTemplate() = KafkaTemplate(
        DefaultKafkaProducerFactory<String, Any>(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to server,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
            )
        )
    )

}

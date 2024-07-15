package ch.awae.mycloud.service.audit.kafka

import org.apache.kafka.clients.consumer.*
import org.apache.kafka.common.serialization.*
import org.springframework.beans.factory.annotation.*
import org.springframework.context.annotation.*
import org.springframework.kafka.config.*
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.*

@Configuration
class KafkaConfiguration(
    @Value("\${mycloud.kafka.url}") val kafkaUrl: String,
    @Value("\${audit.consumer-group}") val consumerGroup: String,
) {

    @Bean
    fun auditConsumerFactory(): ConsumerFactory<String, Any> {
        val configProps = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaUrl,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.GROUP_ID_CONFIG to consumerGroup,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            JsonDeserializer.TRUSTED_PACKAGES to "ch.awae.mycloud.audit",
        )
        return DefaultKafkaConsumerFactory(configProps)
    }
    @Bean
    fun auditKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = auditConsumerFactory()
        return factory
    }

}
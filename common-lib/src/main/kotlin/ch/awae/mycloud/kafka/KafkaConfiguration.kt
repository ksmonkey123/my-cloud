package ch.awae.mycloud.kafka

import org.apache.kafka.clients.consumer.*
import org.apache.kafka.clients.producer.*
import org.apache.kafka.common.serialization.*
import org.springframework.beans.factory.annotation.*
import org.springframework.context.annotation.*
import org.springframework.kafka.config.*
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.*
import org.springframework.stereotype.*

@Component
class KafkaConfiguration(
    @Value("\${mycloud.kafka.url}") private val server: String,
    @Value("\${spring.application.name}") private val applicationName: String,
) {

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Any> = KafkaTemplate(
        DefaultKafkaProducerFactory(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to server,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
            )
        )
    )

    @Bean
    fun defaultKafkaConsumerFactory(): DefaultKafkaConsumerFactory<String, Any> {
        return DefaultKafkaConsumerFactory(
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to server,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
                ConsumerConfig.GROUP_ID_CONFIG to "cg-default-${applicationName}",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                JsonDeserializer.TRUSTED_PACKAGES to "*",
            )
        )
    }

    @Bean
    fun kafkaListenerContainerFactory(defaultKafkaConsumerFactory: DefaultKafkaConsumerFactory<String, Any>): ConcurrentKafkaListenerContainerFactory<String, Any> {
        return ConcurrentKafkaListenerContainerFactory<String, Any>().apply {
            consumerFactory = defaultKafkaConsumerFactory
        }
    }


}
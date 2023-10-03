package ch.awae.mycloud.service.canary.service

import ch.awae.mycloud.service.canary.model.*
import org.apache.kafka.clients.producer.*
import org.apache.kafka.common.serialization.*
import org.springframework.beans.factory.annotation.*
import org.springframework.context.annotation.*
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.*
import org.springframework.stereotype.*

@Configuration
class MessageSenderConfiguration(@Value("\${mycloud.kafka.url}") val server: String) {

    @Bean
    fun messageKafka() = KafkaTemplate<String, String>(
        DefaultKafkaProducerFactory(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to server,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
            )
        )
    )
}

@Service
class MessageSender(
    private val kafka: KafkaTemplate<String, String>,
    @Value("\${canary.kafka.topic}")
    private val topic: String
) {

    fun sendFailure(lastRecord: TestRecord?, currentRecord: TestRecord) {
        val message = "Website scan failed!\n\n" +
                "URL: ${currentRecord.site.siteUrl}\nText:" +
                currentRecord.failedTests.fold("") { acc, s -> "$acc\n - $s" }
        kafka.send(topic, message).get()
    }

    fun sendResolved(lastRecord: TestRecord) {
        // TODO: implement resolution messages
    }

}
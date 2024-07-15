package ch.awae.mycloud.service.canary.service

import ch.awae.mycloud.service.canary.model.*
import org.apache.kafka.clients.producer.*
import org.apache.kafka.common.serialization.*
import org.springframework.beans.factory.annotation.*
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.*
import org.springframework.stereotype.*

@Service
class MessageSender(
    @Value("\${mycloud.kafka.url}") private val server: String,
    @Value("\${canary.kafka.topic}") private val topic: String,
) {
    private val kafka = KafkaTemplate<String, String>(
        DefaultKafkaProducerFactory(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to server,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
            )
        )
    )

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
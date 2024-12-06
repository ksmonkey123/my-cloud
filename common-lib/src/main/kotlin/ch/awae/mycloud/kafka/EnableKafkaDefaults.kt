package ch.awae.mycloud.kafka

import org.springframework.context.annotation.*
import org.springframework.kafka.annotation.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@EnableKafka
@Import(KafkaConfiguration::class)
annotation class EnableKafkaDefaults

package ch.awae.mycloud.notification

import ch.awae.mycloud.kafka.*
import org.springframework.context.annotation.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@EnableKafkaDefaults
@Import(NotificationService::class)
annotation class EnableNotification

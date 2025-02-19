package ch.awae.mycloud.audit

import ch.awae.mycloud.kafka.*
import org.springframework.context.annotation.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@EnableKafkaDefaults
@Import(AuditLogAspect::class, AuditLogService::class, KafkaSender::class)
annotation class EnableAuditLog

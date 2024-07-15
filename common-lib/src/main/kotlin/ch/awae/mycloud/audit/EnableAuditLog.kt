package ch.awae.mycloud.audit

import org.springframework.context.annotation.*
import org.springframework.kafka.annotation.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@EnableKafka
@Import(AuditLogAspect::class, AuditLogService::class, KafkaSender::class)
annotation class EnableAuditLog

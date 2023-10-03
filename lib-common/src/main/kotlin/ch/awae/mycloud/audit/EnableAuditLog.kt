package ch.awae.mycloud.audit

import org.springframework.context.annotation.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(
    AuditLogConfiguration::class
)
annotation class EnableAuditLog

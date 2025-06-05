package ch.awae.mycloud.notification

import org.springframework.context.annotation.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(NotificationService::class)
annotation class EnableNotification

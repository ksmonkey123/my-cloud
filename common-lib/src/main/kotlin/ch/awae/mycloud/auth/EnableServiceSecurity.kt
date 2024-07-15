package ch.awae.mycloud.auth

import org.springframework.context.annotation.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(
    ServiceSecurityConfiguration::class
)
annotation class EnableServiceSecurity

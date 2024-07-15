package ch.awae.mycloud.auth

import org.springframework.context.annotation.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(
    ClientSecurityConfiguration::class
)
annotation class EnableClientSecurity

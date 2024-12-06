package ch.awae.mycloud.shedlock

import org.springframework.context.annotation.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(
    ShedlockConfiguration::class
)
annotation class EnableShedlock

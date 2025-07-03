package ch.awae.mycloud.module.auth.domain

import java.time.*

enum class TokenRetentionPolicy(val duration: Duration) {
    SHORT(Duration.ofHours(4)),
    LONG(Duration.ofDays(7)),
}
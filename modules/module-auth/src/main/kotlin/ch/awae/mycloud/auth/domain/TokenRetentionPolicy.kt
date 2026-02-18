package ch.awae.mycloud.auth.domain

import java.time.*

enum class TokenRetentionPolicy(val duration: Duration) {
    SHORT(Duration.ofHours(4)),
    LONG(Duration.ofDays(7)),
}
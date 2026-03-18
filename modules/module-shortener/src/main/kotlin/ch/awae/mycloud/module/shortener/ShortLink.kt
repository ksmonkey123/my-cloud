package ch.awae.mycloud.module.shortener

import com.fasterxml.jackson.annotation.JsonIgnore

data class ShortLink(
    val id: String,
    val targetUrl: String,
    @JsonIgnore
    val username: String,
)


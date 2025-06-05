package ch.awae.mycloud.service.shortener.dto

import ch.awae.mycloud.service.shortener.model.*
import java.time.LocalDateTime

data class ShortLinkDTO(
    val id: String,
    val targetUrl: String,
) {

    constructor(shortLink: ShortLink) : this(
        id = shortLink.id,
        targetUrl = shortLink.targetUrl,
    )

}

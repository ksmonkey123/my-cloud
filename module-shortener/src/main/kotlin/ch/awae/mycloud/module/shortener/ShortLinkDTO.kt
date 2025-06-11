package ch.awae.mycloud.module.shortener

data class ShortLinkDTO(
    val id: String,
    val targetUrl: String,
) {

    constructor(shortLink: ShortLink) : this(
        id = shortLink.id,
        targetUrl = shortLink.targetUrl,
    )

}

package ch.awae.mycloud.module.shortener

interface ShortLinkRepository {
    fun findById(id: String): ShortLink?
    fun save(shortLink: ShortLink)
    fun existsById(id: String): Boolean
    fun deleteByIdAndUsername(id: String, username: String): Boolean
    fun findByUsername(username: String): List<ShortLink>
}
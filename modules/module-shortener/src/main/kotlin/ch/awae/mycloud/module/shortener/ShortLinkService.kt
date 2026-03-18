package ch.awae.mycloud.module.shortener

import ch.awae.mycloud.common.ResourceNotFoundException
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.hibernate.validator.constraints.URL
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import java.util.*
import kotlin.random.Random

private const val LINK_GENERATION_ATTEMPTS = 10

@Transactional
@Validated
@Service
class ShortLinkService(private val repo: ShortLinkRepository) {

    private val base64Encoder = Base64.getUrlEncoder()

    private fun getUnusedShortLink(): String {
        repeat(LINK_GENERATION_ATTEMPTS) {
            // 8 random chars = 6 bytes encoded base 64.
            val randomString = base64Encoder.encodeToString(Random.nextBytes(6))
            // test if string is already used
            if (!repo.existsById(randomString)) {
                return randomString
            }
        }
        throw RuntimeException("failed to obtain free short link within $LINK_GENERATION_ATTEMPTS attempts")
    }

    fun listShortLinks(username: String): List<ShortLink> = repo.findByUsername(username)

    fun createShortLink(@Valid @URL targetUrl: String, username: String): ShortLink {
        val link = ShortLink(
            getUnusedShortLink(),
            targetUrl,
            username,
        )

        repo.save(link)

        return link
    }

    fun deleteShortLink(id: String, username: String) {
        val deleted = repo.deleteByIdAndUsername(id, username)

        if (!deleted) {
            throw ResourceNotFoundException("/links/$id")
        }
    }

}
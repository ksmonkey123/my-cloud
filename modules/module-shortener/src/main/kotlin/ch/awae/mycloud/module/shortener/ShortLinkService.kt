package ch.awae.mycloud.module.shortener

import ch.awae.mycloud.common.*
import jakarta.transaction.*
import jakarta.validation.*
import org.hibernate.validator.constraints.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*
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

    fun listShortLinks(username: String): List<ShortLinkDTO> = repo.findByUsername(username).map(
        ::ShortLinkDTO
    )

    fun createShortLink(@Valid @URL targetUrl: String, username: String): ShortLinkDTO {
        return ShortLinkDTO(
            repo.save(
                ShortLink(
                    getUnusedShortLink(),
                    username,
                    targetUrl
                )
            )
        )
    }

    fun deleteShortLink(id: String, username: String) {
        val link = repo.findByIdAndUsername(id, username) ?: throw ResourceNotFoundException("/links/$id")
        repo.delete(link)
    }

}
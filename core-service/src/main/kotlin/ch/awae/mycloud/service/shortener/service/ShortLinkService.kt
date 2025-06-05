package ch.awae.mycloud.service.shortener.service

import ch.awae.mycloud.common.ResourceNotFoundException
import ch.awae.mycloud.common.auth.AuthInfo
import ch.awae.mycloud.service.shortener.dto.*
import ch.awae.mycloud.service.shortener.model.*
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
        for (i in 1..LINK_GENERATION_ATTEMPTS) {
            // 8 random chars = 6 bytes encoded base 64.
            val randomString = base64Encoder.encodeToString(Random.nextBytes(6))
            // test if string is already used
            if (!repo.existsById(randomString)) {
                return randomString
            }
        }
        throw RuntimeException("failed to obtain free short link within $LINK_GENERATION_ATTEMPTS attempts")
    }

    fun listShortLinks(): List<ShortLinkDTO> = repo.findByUsername(AuthInfo.username!!).map(::ShortLinkDTO)

    fun createShortLink(@Valid @URL targetUrl: String): ShortLinkDTO {
        return ShortLinkDTO(repo.save(ShortLink(getUnusedShortLink(), AuthInfo.username!!, targetUrl)))
    }

    fun deleteShortLink(id: String) {
        val link = repo.findByIdAndUsername(id, AuthInfo.username!!) ?: throw ResourceNotFoundException("/links/$id")
        repo.delete(link)
    }

}
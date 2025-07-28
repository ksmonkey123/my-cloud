package ch.awae.mycloud.common

import java.security.*
import java.util.*

object TokenGenerator {

    /**
     * @param collisionCheck: if provided, a `true` result indicates a collision
     */
    fun generate(
        bytes: Int,
        type: EncoderType = EncoderType.BASIC_PADDED,
        collisionCheck: ((String) -> Boolean)? = null,
        maxAttempts: Int = 10,
    ): String {
        val encoder = when (type) {
            EncoderType.URL -> Base64.getUrlEncoder().withoutPadding()
            EncoderType.URL_PADDED -> Base64.getUrlEncoder()
            EncoderType.BASIC -> Base64.getEncoder().withoutPadding()
            EncoderType.BASIC_PADDED -> Base64.getEncoder()
        }

        val array = ByteArray(bytes)
        for (i in 0 until maxAttempts) {
            SecureRandom().nextBytes(array)
            val token = encoder.encodeToString(array)
            if (collisionCheck == null || !collisionCheck(token)) {
                // no collision check or no collision
                return token
            }
        }
        throw IllegalArgumentException("unable to generate token")
    }

    enum class EncoderType {
        URL,
        URL_PADDED,
        BASIC,
        BASIC_PADDED,
    }

}

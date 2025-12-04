package ch.awae.mycloud.api.common

import java.util.*

interface TokenGenerator {

    /**
     * generate a random base64 encoded token
     *
     * @param bytes the number of bytes of initial random data.
     * @param type the type of encoder to use.
     * @param collisionCheck an optional callback for indicating collisions. a `true` result indicates a collision.
     * @param maxAttempts the number of times a token generation is attempted before giving up
     * @throws TokenGenerationException if no valid token was generated in the [maxAttempts] attempts to do so.
     */
    fun generate(
        bytes: Int,
        type: EncoderType = EncoderType.BASIC_PADDED,
        collisionCheck: ((String) -> Boolean)? = null,
        maxAttempts: Int = 10
    ): String

    enum class EncoderType(val encoder: Base64.Encoder) {
        URL(Base64.getUrlEncoder().withoutPadding()),
        URL_PADDED(Base64.getUrlEncoder()),
        BASIC(Base64.getEncoder().withoutPadding()),
        BASIC_PADDED(Base64.getEncoder()),
    }

}

class TokenGenerationException(message: String) : RuntimeException(message)
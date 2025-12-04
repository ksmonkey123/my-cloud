package ch.awae.mycloud

import ch.awae.mycloud.api.common.*
import org.springframework.stereotype.*
import java.security.*

@Service
class TokenGeneratorService : TokenGenerator {

    override fun generate(
        bytes: Int,
        type: TokenGenerator.EncoderType,
        collisionCheck: ((String) -> Boolean)?,
        maxAttempts: Int
    ): String {
        val encoder = type.encoder
        val array = ByteArray(bytes)
        for (i in 0 until maxAttempts) {
            SecureRandom().nextBytes(array)
            val token = encoder.encodeToString(array)
            if (collisionCheck == null || !collisionCheck(token)) {
                // no collision check or no collision
                return token
            }
        }
        throw TokenGenerationException("unable to generate token after $maxAttempts attempts")
    }

}

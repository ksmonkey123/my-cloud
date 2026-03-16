package ch.awae.mycloud.common.util

import java.nio.ByteBuffer
import java.security.SecureRandom
import java.time.Instant
import java.util.*

@JvmInline
value class GUID(val uuid: UUID) {

    /**
     * converts the GUID into a base64 string representation.
     *
     * Before encoding, the upper half of the UUID is XOR-encoded with the lower half.
     * In UUIDv7, the first 48 bits encode the timestamp and are therefore quite static.
     * The XOR-encoding "scrambles" the timestamp with effectively random noise from the lower half.
     * This yields more random looking strings.
     */
    fun toShortString(): String {
        val buffer = ByteBuffer.allocate(16)
        buffer.putLong(uuid.mostSignificantBits xor uuid.leastSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
        return encoder.encodeToString(buffer.array())
    }

    companion object {

        private val encoder = Base64.getUrlEncoder().withoutPadding()
        private val decoder = Base64.getUrlDecoder()

        private val random: ThreadLocal<SecureRandom> = ThreadLocal.withInitial(::SecureRandom)

        fun generateV7(timestamp: Instant = Instant.now()): GUID {
            val timestamp = timestamp.toEpochMilli()

            require(timestamp.ushr(48) == 0L) { "Timestamp exceeds 48-bit range needed for UUIDv7" }

            val rand = random.get()

            val randA = rand.nextInt()
            val randB = rand.nextLong()

            val mostSigBits = (timestamp shl 16) or (randA.toLong() and 0xFFFL)

            return GUID(
                UUID(
                    (mostSigBits and 0xFFFFFFFFFFFF0FFFu.toLong()) or 0x0000000000007000,
                    (randB and 0x3FFFFFFFFFFFFFFF) or 0x8000000000000000u.toLong()
                )
            )
        }

        /**
         * decodes short strings encoded by [GUID.toShortString]
         */
        fun decodeShortString(value: String): GUID {
            val bytes = decoder.decode(value)

            require(bytes != null && bytes.size == 16) { "provided string does not encode exactly 16 bytes." }

            val buffer = ByteBuffer.wrap(bytes)
            val msb = buffer.getLong()
            val lsb = buffer.getLong()

            return GUID(UUID(msb xor lsb, lsb))
        }

    }
}
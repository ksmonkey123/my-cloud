package ch.awae.mycloud.common.util

import java.security.SecureRandom
import java.time.Instant
import java.util.*

object GUID {

    private val random: ThreadLocal<SecureRandom> = ThreadLocal.withInitial(::SecureRandom)

    fun generateV7(timestamp: Instant = Instant.now()): UUID {
        val timestamp = timestamp.toEpochMilli()

        require(timestamp.ushr(48) == 0L) { "Timestamp exceeds 48-bit range needed for UUIDv7" }

        val rand = random.get()

        val randA = rand.nextInt()
        val randB = rand.nextLong()

        val mostSigBits = (timestamp shl 16) or (randA.toLong() and 0xFFFL)

        return UUID(
            (mostSigBits and 0xFFFFFFFFFFFF0FFFu.toLong()) or 0x0000000000007000,
            (randB and 0x3FFFFFFFFFFFFFFF) or 0x8000000000000000u.toLong()
        )
    }

}
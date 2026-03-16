package ch.awae.mycloud.common.util

import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class GUIDTest {

    @Test
    fun `uuid encoded to short string must be decodable`() {
        val uuid = GUID(UUID.randomUUID())
        val shortString = uuid.toShortString()
        assertEquals(22, shortString.length)
        assertEquals(uuid, GUID.decodeShortString(shortString))
    }

}
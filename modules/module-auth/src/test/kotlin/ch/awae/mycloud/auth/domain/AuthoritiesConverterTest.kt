package ch.awae.mycloud.auth.domain

import org.junit.jupiter.api.Test
import kotlin.test.*

class AuthoritiesConverterTest {

    val converter = AuthoritiesConverter()

    @Test
    fun testNullMarshalling() {
        assertEquals("[]", converter.convertToDatabaseColumn(null))
    }

    @Test
    fun testMarshalling() {
        val data = setOf("a", "b", "c", "d")
        val result = AuthoritiesConverter().convertToDatabaseColumn(data)

        assertEquals("""["a","b","c","d"]""", result)
    }

    @Test
    fun testNullUnmarshalling() {
        assertTrue(converter.convertToEntityAttribute(null).isEmpty())
    }

    @Test
    fun testEmptyUnmarshalling() {
        assertTrue(converter.convertToEntityAttribute("[]").isEmpty())
    }

    @Test
    fun testUnmarshalling() {
        val result = converter.convertToEntityAttribute("""["a","b","c","d"]""")

        assertEquals(setOf("a", "b", "c", "d"), result)
    }

}
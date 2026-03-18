package ch.awae.mycloud.module.documents

import ch.awae.mycloud.documents.DocumentData
import ch.awae.mycloud.documents.DocumentSource
import ch.awae.mycloud.test.ModuleTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class DocumentStoreImplTest : ModuleTest() {

    @Autowired
    lateinit var documentDataStore: DocumentDataStore

    @ParameterizedTest
    @EnumSource(DocumentSource::class)
    fun `document can be stored`(source: DocumentSource) {
        // arrange
        val expiration = LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.MICROS)
        val data = "this is a test file".toByteArray()
        val document = DocumentData(
            source,
            "testFile.txt",
            MediaType.TEXT_PLAIN,
            expiration,
            data,
        )

        // act
        val id = documentDataStore.createDocument(document, "dummy")
        val retrieved = documentDataStore.retrieveById(id.id)

        // assert
        assertNotNull(retrieved)
        assertEquals(DocumentSource.BOOKKEEPING, retrieved.source)
        assertEquals("testFile.txt", retrieved.filename)
        assertEquals(MediaType.TEXT_PLAIN, retrieved.type)
        assertEquals(expiration, retrieved.validUntil)
        assertContentEquals(data, retrieved.content)
    }

    @Test
    fun `expired document is not retrieved`() {
        // arrange
        val expiration = LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.MICROS)
        val data = "this is a test file".toByteArray()
        val document = DocumentData(
            DocumentSource.BOOKKEEPING,
            "testFile.txt",
            MediaType.TEXT_PLAIN,
            expiration,
            data,
        )

        // act
        val id = documentDataStore.createDocument(document, "dummy")
        val retrieved = documentDataStore.retrieveById(id.id)

        // assert
        assertNull(retrieved)
    }

    @Test
    fun `cleanup removes old documents`() {
        // arrange
        val document = DocumentData(
            DocumentSource.BOOKKEEPING,
            "testFile.txt",
            MediaType.TEXT_PLAIN,
            LocalDateTime.now().minusHours(1),
            "this is a test file".toByteArray(),
        )

        val identifier = documentDataStore.createDocument(document, "dummy")

        assertEquals(
            1,
            sql.queryForObject(
                "select count(*) from documents.document where id = :id",
                mapOf("id" to identifier.id),
                Long::class.java
            )
        )

        // act
        documentDataStore.deleteExpired()

        // assert
        assertEquals(
            0,
            sql.queryForObject(
                "select count(*) from documents.document where id = :id",
                mapOf("id" to identifier.id),
                Long::class.java
            )
        )

    }

    @Test
    fun `cleanup does not remove valid documents`() {
        // arrange
        val document = DocumentData(
            DocumentSource.BOOKKEEPING,
            "testFile.txt",
            MediaType.TEXT_PLAIN,
            LocalDateTime.now().plusHours(1),
            "this is a test file".toByteArray(),
        )

        val identifier = documentDataStore.createDocument(document, "dummy")

        assertEquals(
            1,
            sql.queryForObject(
                "select count(*) from documents.document where id = :id",
                mapOf("id" to identifier.id),
                Long::class.java
            )
        )

        // act
        documentDataStore.deleteExpired()

        // assert
        assertEquals(
            1,
            sql.queryForObject(
                "select count(*) from documents.document where id = :id",
                mapOf("id" to identifier.id),
                Long::class.java
            )
        )

    }


}
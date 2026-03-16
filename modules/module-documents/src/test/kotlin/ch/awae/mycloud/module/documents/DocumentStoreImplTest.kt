package ch.awae.mycloud.module.documents

import ch.awae.mycloud.documents.DocumentSource
import ch.awae.mycloud.test.ModuleTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import java.time.LocalDateTime

class DocumentStoreImplTest : ModuleTest() {

    @Autowired
    lateinit var documentStore: DocumentStoreImpl

    @Autowired
    lateinit var documentRepository: DocumentRepository

    @Test
    fun `document is stored`() {
        // arrange
        sql.update("truncate documents.document", emptyMap<String, Any>())

        // act
        val expiration = LocalDateTime.now().plusHours(1)
        val id = documentStore.createDocument(
            DocumentSource.BOOKKEEPING,
            "testFile.txt",
            MediaType.TEXT_PLAIN,
            "this is a test file".toByteArray(Charsets.UTF_8),
            expiration,
            "dummy",
        )

        // assert
        val retrieved = documentRepository.findValid()
    }

}
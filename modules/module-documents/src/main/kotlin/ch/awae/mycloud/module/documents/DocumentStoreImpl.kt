package ch.awae.mycloud.module.documents

import ch.awae.mycloud.common.util.GUID
import ch.awae.mycloud.documents.DocumentIdentifier
import ch.awae.mycloud.documents.DocumentSource
import ch.awae.mycloud.documents.DocumentStore
import jakarta.transaction.Transactional
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Transactional
class DocumentStoreImpl(
    private val documentRepository: DocumentRepository,
) : DocumentStore {

    override fun createDocument(
        source: DocumentSource,
        filename: String,
        type: MediaType,
        content: ByteArray,
        validUntil: LocalDateTime,
        username: String,
    ): DocumentIdentifier {
        val document = DocumentData(
            id = GUID.generateV7(),
            filename = filename,
            type = type,
            content = content,
            validUntil = validUntil,
            source = source,
        )
        documentRepository.create(document, username)

        return DocumentIdentifier(document.id.toShortString(), type.toString())
    }

    @SchedulerLock(name = "documents:expired-documents-cleaner")
    @Scheduled(cron = "\${documents.clean-timer.schedule}")
    fun cleanOldTokens() {
        documentRepository.deleteExpired()
    }

}
package ch.awae.mycloud.module.documents

import ch.awae.mycloud.documents.DocumentIdentifier
import ch.awae.mycloud.documents.DocumentSource
import ch.awae.mycloud.documents.DocumentStore
import ch.awae.mycloud.common.TokenGenerator
import jakarta.transaction.Transactional
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
@Transactional
class DocumentStoreImpl(
    private val documentRepository: DocumentRepository,
    private val tokenGenerator: TokenGenerator,
) : DocumentStore {

    override fun createDocument(
        source: DocumentSource,
        filename: String,
        type: MediaType,
        content: ByteArray,
        lifetime: Duration,
        username: String,
    ): DocumentIdentifier {
        return createDocument(
            filename = filename,
            type = type,
            content = content,
            validUntil = LocalDateTime.now().plus(lifetime),
            source = source,
            username = username,
        )
    }

    override fun createDocument(
        source: DocumentSource,
        filename: String,
        type: MediaType,
        content: ByteArray,
        validUntil: LocalDateTime,
        username: String,
    ): DocumentIdentifier {
        val token = tokenGenerator.generate(16, TokenGenerator.EncoderType.URL)
        val document = DocumentEntity(
            token = token,
            filename = filename,
            type = type.toString(),
            content = content,
            createdAt = LocalDateTime.now(),
            validUntil = validUntil,
            source = source,
            username = username,
        )
        documentRepository.save(document)

        return DocumentIdentifier("/documents/$token", type.toString())
    }

    @SchedulerLock(name = "documents:expired-documents-cleaner")
    @Scheduled(cron = "\${documents.clean-timer.schedule}")
    fun cleanOldTokens() {
        documentRepository.deleteExpired()
    }

}
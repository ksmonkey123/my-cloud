package ch.awae.mycloud.module.documents

import ch.awae.mycloud.common.TokenGenerator
import ch.awae.mycloud.api.documents.*
import jakarta.transaction.*
import net.javacrumbs.shedlock.spring.annotation.*
import org.springframework.http.*
import org.springframework.scheduling.annotation.*
import org.springframework.stereotype.*
import java.time.*

@Service
@Transactional
class DocumentStoreImpl(
    private val documentRepository: DocumentRepository,
    private val tokenGenerator: TokenGenerator,
) : DocumentStore {

    override fun createDocument(filename: String, type: MediaType, content: ByteArray, lifetime: Duration): DocumentIdentifier {
        val token = tokenGenerator.generate(16, TokenGenerator.EncoderType.URL)
        val document = DocumentEntity(
            token = token,
            filename = filename,
            type = type.toString(),
            content = content,
            validUntil = LocalDateTime.now().plus(lifetime),
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
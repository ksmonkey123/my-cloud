package ch.awae.mycloud.module.documents

import java.util.*

interface DocumentRepository {
    fun save(document: Document)
    fun deleteExpired()
    fun findValidById(id: UUID): Document?
}

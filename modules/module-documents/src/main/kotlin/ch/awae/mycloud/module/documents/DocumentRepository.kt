package ch.awae.mycloud.module.documents

import ch.awae.mycloud.common.util.GUID

interface DocumentRepository {
    fun save(document: Document)
    fun deleteExpired()
    fun findValidById(id: GUID): Document?
}

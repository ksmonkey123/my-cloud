package ch.awae.mycloud.documents

interface DocumentStore {

    fun createDocument(
        document: DocumentData,
        username: String,
    ): DocumentIdentifier

}


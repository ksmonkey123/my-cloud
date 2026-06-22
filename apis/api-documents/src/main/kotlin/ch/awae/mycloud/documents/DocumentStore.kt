package ch.awae.mycloud.documents

@Suppress("kotlin:S6517")
interface DocumentStore {

    fun createDocument(
        document: DocumentData,
        username: String,
    ): DocumentIdentifier

}


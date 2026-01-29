package ch.awae.mycloud.api.email

/**
 * Represents an email message to be sent using an email delivery service.
 *
 * @property recipient The email address of the recipient.
 * @property subject The subject line of the email.
 * @property body The content of the email, represented as an instance of a class implementing the `EmailBody` interface.
 * @property uid An optional unique identifier for the email, used for deduplication.
 */
data class EmailMessage(
    val recipient: String,
    val subject: String,
    val body: Body,
    val uid: String? = null,
) {

    sealed interface Body
    data class PlaintextBody(val text: String) : Body
    data class HtmlBody(val text: String) : Body
    data class MarkdownBody(val text: String) : Body
}
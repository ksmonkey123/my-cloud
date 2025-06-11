package ch.awae.mycloud.api.email

data class EmailMessage(
    val sender: String,
    val recipient: String,
    val subject: String,
    val body: EmailBody,
)

sealed interface EmailBody
data class PlainBody(val text: String) : EmailBody
data class HtmlBody(val text: String) : EmailBody
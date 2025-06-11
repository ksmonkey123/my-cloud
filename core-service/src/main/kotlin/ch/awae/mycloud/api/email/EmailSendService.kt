package ch.awae.mycloud.api.email

interface EmailSendService {
    fun send(email: EmailMessage)
}


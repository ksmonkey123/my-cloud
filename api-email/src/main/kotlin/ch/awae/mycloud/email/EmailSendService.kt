package ch.awae.mycloud.email

interface EmailSendService {
    /**
     * Registers an email message for sending.
     *
     * @param email The email message to send.
     * @return true if the email was registered, false if the message was ignored due to deduplication.
     */
    fun send(email: EmailMessage): Boolean
}


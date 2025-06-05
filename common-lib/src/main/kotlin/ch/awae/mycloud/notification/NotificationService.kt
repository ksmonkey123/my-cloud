package ch.awae.mycloud.notification

interface NotificationService {
    fun send(message: NotificationMessage)
}

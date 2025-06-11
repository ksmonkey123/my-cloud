package ch.awae.mycloud.api.notification

interface NotificationService {
    fun sendPlaintextNotification(message: NotificationMessage)
}

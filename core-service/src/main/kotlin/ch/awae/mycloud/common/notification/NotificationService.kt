package ch.awae.mycloud.common.notification

interface NotificationService {
    fun send(message: NotificationMessage)
}

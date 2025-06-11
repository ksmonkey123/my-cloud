package ch.awae.mycloud.service.canary

import ch.awae.mycloud.api.notification.*
import org.springframework.stereotype.*

@Service
class MessageSender(
    private val notificationService: NotificationService,
) {

    fun sendMessage(title: String, message: String) {
        notificationService.sendPlaintextNotification(NotificationMessage(title, message))
    }

}

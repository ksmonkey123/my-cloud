package ch.awae.mycloud.module.docker.dockerhub.service

import ch.awae.mycloud.api.email.*
import ch.awae.mycloud.module.docker.dockerhub.*
import ch.awae.mycloud.module.docker.dockerhub.model.*
import org.springframework.beans.factory.annotation.*
import org.springframework.stereotype.*

@Service
class UpdateAnnouncer(
    private val dockerProperties: DockerhubProperties,
    private val emailSendService: EmailSendService,
    @Value("\${canary.sender}")
    private val sender: String,
) {

    fun announceUpdate(recipient: String, item: MonitoredEntry, oldTags: Set<String>, newTags: Set<String>) {
        val unchangedTags = oldTags.intersect(newTags)
        val addedTags = newTags.subtract(oldTags)
        val removedTags = oldTags.subtract(newTags)

        val message = "docker image `" + item.descriptor + "` updated." +
                enumerate("\n\nadded tags:", addedTags) +
                enumerate("\n\nremoved tags:", removedTags) +
                enumerate("\n\nunchanged tags:", unchangedTags) +
                "\n\n<${dockerProperties.webUrl}/${item.webIdentifier}/tags>"

        emailSendService.send(
            EmailMessage(
                sender = sender,
                recipient = recipient,
                subject = "new docker image available for ${item.descriptor}",
                body = MarkdownBody(message),
            )
        )
    }

    private fun enumerate(listHeader: String, items: Set<String>): String {
        return if (items.isEmpty()) "" else items.fold(listHeader) { acc, item -> "$acc\n * `$item`" }
    }

}
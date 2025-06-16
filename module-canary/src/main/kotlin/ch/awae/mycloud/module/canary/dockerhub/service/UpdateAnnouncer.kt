package ch.awae.mycloud.module.canary.dockerhub.service

import ch.awae.mycloud.api.email.*
import ch.awae.mycloud.module.canary.dockerhub.*
import ch.awae.mycloud.module.canary.dockerhub.model.*
import org.springframework.beans.factory.annotation.*
import org.springframework.stereotype.*

@Service
class UpdateAnnouncer(
    private val dockerProperties: DockerProperties,
    private val emailSendService: EmailSendService,
    @Value("\${canary.sender}")
    private val sender: String,
) {

    fun announceUpdate(recipient: String, item: MonitoredEntry, oldTags: Set<String>, newTags: Set<String>) {
        val unchangedTags = oldTags.intersect(newTags)
        val addedTags = newTags.subtract(oldTags)
        val removedTags = oldTags.subtract(newTags)

        val message = "Docker image <span style=\"font-family: monospace;\">" + item.descriptor + "</span> updated.</p><br/>" +
                enumerate("<br/>added tags:", addedTags) +
                enumerate("<br/>removed tags:", removedTags) +
                enumerate("<br/>unchanged tags:", unchangedTags) +
                "<br/><br/>${dockerProperties.webUrl}/${item.webIdentifier}/tags"

        emailSendService.send(
            EmailMessage(
                sender = sender,
                recipient = recipient,
                subject = "new docker image available for ${item.descriptor}",
                body = HtmlBody(message),
            )
        )
    }

    private fun enumerate(listHeader: String, items: Set<String>): String {
        return if (items.isEmpty()) "" else {
            "<ul>" + items.fold(listHeader) { acc, item -> "$acc<li><span style=\"font-family: monospace;\">$item</span></li>" } + "</ul>"
        }
    }

}
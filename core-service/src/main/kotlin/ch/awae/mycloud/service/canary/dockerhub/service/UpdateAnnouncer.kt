package ch.awae.mycloud.service.canary.dockerhub.service

import ch.awae.mycloud.service.canary.*
import ch.awae.mycloud.service.canary.dockerhub.*
import ch.awae.mycloud.service.canary.dockerhub.model.*
import org.springframework.stereotype.*

@Service
class UpdateAnnouncer(
    private val messageSender: MessageSender,
    private val dockerProperties: DockerProperties,
) {

    fun announceUpdate(item: MonitoredEntry, oldTags: Set<String>, newTags: Set<String>) {
        val unchangedTags = oldTags.intersect(newTags)
        val addedTags = newTags.subtract(oldTags)
        val removedTags = oldTags.subtract(newTags)

        val message = "docker image <code>" + item.descriptor + "</code> updated.\n" +
                enumerate("\nadded tags:", addedTags) +
                enumerate("\nremoved tags:", removedTags) +
                enumerate("\nunchanged tags:", unchangedTags) +
                "\n\n${dockerProperties.webUrl}/${item.webIdentifier}/tags"

        messageSender.sendMessage("new docker image available for ${item.descriptor}", message)
    }

    private fun enumerate(listHeader: String, items: Set<String>): String {
        return if (items.isEmpty()) "" else items.fold(listHeader) { acc, item -> "$acc\n - <code>$item</code>" }
    }

}
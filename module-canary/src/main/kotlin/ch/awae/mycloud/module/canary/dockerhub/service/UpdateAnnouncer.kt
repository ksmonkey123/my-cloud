package ch.awae.mycloud.module.canary.dockerhub.service

import ch.awae.mycloud.module.canary.MessageSender
import ch.awae.mycloud.module.canary.dockerhub.DockerProperties
import ch.awae.mycloud.module.canary.dockerhub.model.MonitoredEntry
import org.springframework.stereotype.*
import kotlin.collections.fold
import kotlin.collections.intersect
import kotlin.collections.subtract

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
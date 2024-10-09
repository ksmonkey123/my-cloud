package ch.awae.mycloud.service.canary.dockerhub.service

import ch.awae.mycloud.*
import ch.awae.mycloud.service.canary.dockerhub.model.*
import jakarta.transaction.*
import org.springframework.data.repository.*
import org.springframework.stereotype.*

@Service
@Transactional
class DockerhubScanService(
    private val monitoredEntryRepository: MonitoredEntryRepository,
    private val updateAnnouncer: UpdateAnnouncer,
    private val entryStateRepository: EntryStateRepository,
) {

    private val logger = createLogger()

    fun runScan(id: Long, client: DockerhubApiClient) {
        val entry = monitoredEntryRepository.findByIdOrNull(id)?.takeIf { it.enabled }
            ?: throw ResourceNotFoundException("monitored_entry($id)")

        val lastTagSet = entry.entryStates.maxByOrNull { it.creationTimestamp }?.let {
            TagSet(it.digest, it.tags.toSet())
        }
        val newTagSet = loadTagSet(entry, client)

        if (lastTagSet == null || newTagSet != lastTagSet) {
            if (entry.tagChangesOnly && (newTagSet.tags == lastTagSet?.tags)) {
                logger.info("\"${entry.descriptor}\" tags unchanged, only requesting tag changes")
                return
            }
            // mismatch found, process update
            logger.info("\"${entry.descriptor}\" relevant changes detected")
            updateAnnouncer.announceUpdate(entry, lastTagSet?.tags ?: emptySet(), newTagSet.tags)
            entryStateRepository.save(EntryState(entry, newTagSet.digest, newTagSet.tags.toList()))
        } else {
            logger.info("\"${entry.descriptor}\" unchanged")
            return
        }
    }

    private fun loadTagSet(entry: MonitoredEntry, client: DockerhubApiClient): TagSet {
        try {
            val tags = client.getTagList(entry.namespace, entry.repository)

            // find the watched tag in the result set
            val referenceTag = tags.values.flatten().find { it.tag == entry.tag }
                ?: throw IllegalArgumentException("unable to find watched tag ${entry.tag} in tag for ${entry.descriptor}").also {
                    logger.warn(it.message)
                }

            // find all tags with the same digest as the reference tag
            val relevantTags = tags[referenceTag.digest]!!.map { it.tag }

            logger.debug("identified {} relevant tags: {}", relevantTags.size, relevantTags)

            return TagSet(referenceTag.digest, relevantTags.toSet())
        } catch (e: Exception) {
            logger.warn("unable to load tag list for ${entry.descriptor}", e)
            throw IllegalArgumentException("unable to load tag list for ${entry.descriptor}")
        }
    }

    data class TagSet(val digest: String, val tags: Set<String>)

}

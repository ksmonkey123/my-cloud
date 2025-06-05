package ch.awae.mycloud.service.canary.dockerhub.service

import ch.awae.mycloud.common.ResourceNotFoundException
import ch.awae.mycloud.service.canary.dockerhub.dto.*
import ch.awae.mycloud.service.canary.dockerhub.model.*
import jakarta.transaction.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import java.time.*

@Transactional
@Service
class DockerService(
    val monitoredEntryRepository: MonitoredEntryRepository,
    val entryStateRepository: EntryStateRepository,
) {

    fun listAll(): List<DockerImageSummary> {
        return monitoredEntryRepository.findAll()
            .sortedByDescending { it.currentState?.recordedAt ?: LocalDateTime.MAX }
            .map {
                DockerImageSummary(
                    identifier = it.webIdentifier,
                    tag = it.tag,
                    enabled = it.enabled,
                    state = it.currentState?.let { s ->
                        DockerImageSummary.State(
                            digest = s.digest,
                            tags = s.tags.sorted(),
                            recordedAt = s.recordedAt.toString().replace("T", " "),
                        )
                    },
                )
            }
    }

    fun getDetails(namespace: String?, repository: String, tag: String): DockerImageDetails {
        val entry = monitoredEntryRepository.findByIdentifier(namespace, repository, tag)
            ?: throw ResourceNotFoundException("/docker/image/${namespace ?: '_'}:$repository:$tag")

        val states = entryStateRepository.findByMonitoredEntry(entry, Pageable.ofSize(100))

        return DockerImageDetails(
            identifier = entry.webIdentifier,
            tag = entry.tag,
            tagsChangesOnly = entry.tagChangesOnly,
            enabled = entry.enabled,
            states = states.map {
                DockerImageDetails.State(
                    digest = it.digest,
                    tags = it.tags.sorted(),
                    recordedAt = it.creationTimestamp.toString().replace("T", " "),
                )
            }
        )

    }


}
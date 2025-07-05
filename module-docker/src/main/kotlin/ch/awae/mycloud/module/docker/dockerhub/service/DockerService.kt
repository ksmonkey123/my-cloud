package ch.awae.mycloud.module.docker.dockerhub.service

import ch.awae.mycloud.common.ResourceNotFoundException
import ch.awae.mycloud.module.docker.dockerhub.dto.DockerImageDetails
import ch.awae.mycloud.module.docker.dockerhub.dto.DockerImageSummary
import ch.awae.mycloud.module.docker.dockerhub.model.EntryStateRepository
import ch.awae.mycloud.module.docker.dockerhub.model.MonitoredEntryRepository
import jakarta.transaction.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import java.time.*
import kotlin.collections.map
import kotlin.collections.sorted
import kotlin.collections.sortedByDescending
import kotlin.let

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
package ch.awae.mycloud.service.canary.dockerhub.service

import ch.awae.mycloud.service.canary.dockerhub.dto.*
import ch.awae.mycloud.service.canary.dockerhub.model.*
import jakarta.transaction.*
import org.springframework.stereotype.*
import org.springframework.util.comparator.Comparators
import java.time.LocalDateTime

@Transactional
@Service
class DockerService(
    val monitoredEntryRepository: MonitoredEntryRepository
) {

    fun listAll(): List<DockerImageSummary> {
        return monitoredEntryRepository.findAll()
            .sortedByDescending { it.currentState?.recordedAt ?: LocalDateTime.MAX }
            .map {
                DockerImageSummary(
                    identifier = it.webIdentifier,
                    tag = it.tag,
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

}
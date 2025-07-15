package ch.awae.mycloud.module.docker.dockerhub.facade

import ch.awae.mycloud.module.docker.dockerhub.dto.DockerImageDetails
import ch.awae.mycloud.module.docker.dockerhub.dto.DockerImageSummary
import ch.awae.mycloud.module.docker.dockerhub.service.DockerService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import kotlin.takeUnless

@RestController
@RequestMapping("/rest/docker")
@PreAuthorize("hasAuthority('docker')")
class DockerRestController(
    val dockerService: DockerService,
) {

    @GetMapping("/images")
    fun listAll(): List<DockerImageSummary> {
        return dockerService.listAll()
    }

    @GetMapping("/images/{namespace}:{repository}:{tag}")
    fun getImageDetails(
        @PathVariable namespace: String,
        @PathVariable repository: String,
        @PathVariable tag: String,
    ): DockerImageDetails {
        return dockerService.getDetails(
            namespace.takeUnless { it == "_" },
            repository,
            tag,
        )
    }


}


package ch.awae.mycloud.module.canary.dockerhub.facade

import ch.awae.mycloud.module.canary.dockerhub.dto.DockerImageDetails
import ch.awae.mycloud.module.canary.dockerhub.dto.DockerImageSummary
import ch.awae.mycloud.module.canary.dockerhub.service.DockerService
import org.springframework.web.bind.annotation.*
import kotlin.takeUnless

@RestController
@RequestMapping("/rest/canary/docker")
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


package ch.awae.mycloud.service.canary.dockerhub.facade

import ch.awae.mycloud.service.canary.dockerhub.dto.*
import ch.awae.mycloud.service.canary.dockerhub.service.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/canary/docker")
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


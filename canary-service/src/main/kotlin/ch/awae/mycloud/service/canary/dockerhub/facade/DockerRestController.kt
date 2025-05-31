package ch.awae.mycloud.service.canary.dockerhub.facade

import ch.awae.mycloud.service.canary.dockerhub.dto.*
import ch.awae.mycloud.service.canary.dockerhub.service.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/docker")
class DockerRestController(
    val dockerService: DockerService,
) {

    @GetMapping("/images")
    fun listAll(): List<DockerImageSummary> {
        return dockerService.listAll()
    }

}


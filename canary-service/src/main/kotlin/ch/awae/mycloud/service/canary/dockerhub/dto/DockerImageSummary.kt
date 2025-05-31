package ch.awae.mycloud.service.canary.dockerhub.dto

data class DockerImageSummary(
    val identifier: String,
    val tag: String,
    val state: State?,
) {
    data class State(val digest: String, val tags: List<String>, val recordedAt: String)
}
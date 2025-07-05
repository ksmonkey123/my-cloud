package ch.awae.mycloud.module.docker.dockerhub.dto

data class DockerImageSummary(
    val identifier: String,
    val tag: String,
    val state: State?,
    val enabled: Boolean,
) {
    data class State(val digest: String, val tags: List<String>, val recordedAt: String)
}
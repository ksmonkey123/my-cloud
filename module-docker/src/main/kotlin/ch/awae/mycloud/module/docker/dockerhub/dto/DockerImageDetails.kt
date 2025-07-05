package ch.awae.mycloud.module.docker.dockerhub.dto

data class DockerImageDetails(
    val identifier: String,
    val tag: String,
    val tagsChangesOnly: Boolean,
    val enabled: Boolean,
    val states: List<State>
) {
    data class State(
        val digest: String,
        val tags: List<String>,
        val recordedAt: String,
    )
}
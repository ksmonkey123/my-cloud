package ch.awae.mycloud.service.canary.dockerhub.service

import ch.awae.mycloud.common.ExpiringInstance
import ch.awae.mycloud.common.createLogger
import ch.awae.mycloud.service.canary.dockerhub.*
import org.springframework.boot.web.client.*
import org.springframework.stereotype.*
import org.springframework.web.client.*
import kotlin.time.Duration.Companion.days

@Repository
class DockerhubApiClient(private val dockerProperties: DockerProperties) {

    private val logger = createLogger()

    private val http = ExpiringInstance(1.days, ::buildRestTemplate)
    private val apiUrl = dockerProperties.apiUrl

    fun getTagList(namespace: String?, repository: String): Map<String, List<Tag>> {
        logger.debug("loading tags for ${namespace ?: "_"}/$repository")
        val rawTags =
            fetchTags("$apiUrl/namespaces/${namespace ?: "library"}/repositories/${repository}/tags?page_size=100")
        val tags = rawTags.filter { it.digest != null }.map { Tag(it.name, it.digest!!) }
        val digests = tags.groupBy { it.digest }
        logger.debug("found ${digests.size} digests in ${tags.size} processable tags (${rawTags.size} tags in total)")
        return digests
    }

    private tailrec fun fetchTags(
        url: String?,
        previousResults: Set<TagListResult> = emptySet(),
        invocationCounter: Int = 1,
    ): Set<TagListResult> {
        if (url == null) {
            return previousResults
        }

        val response = http.instance.getForObject(url, TagListResponse::class.java)!!
        return fetchTags(response.next, previousResults + response.results, invocationCounter + 1)
    }

    private fun buildRestTemplate(): RestTemplate {
        logger.info("creating restTemplate for dockerhub API")
        val httpBuilder = RestTemplateBuilder().defaultHeader("Content-Type", "application/json")

        // initial request to get an auth token
        val request = mapOf(
            "username" to dockerProperties.username,
            "password" to dockerProperties.password
        )
        val token = httpBuilder.build()
            .postForObject("$apiUrl/users/login", request, LoginResponse::class.java)?.token
            ?: throw IllegalStateException("missing auth token for dockerhub")

        logger.info("successfully logged into dockerhub")

        // attach auth token to template by default
        return httpBuilder.defaultHeader("Authorization", "JWT $token").build()
    }
}

data class Tag(val tag: String, val digest: String)

private class TagListResponse {
    var next: String? = null
    lateinit var results: List<TagListResult>
}

private class TagListResult {
    lateinit var name: String
    var digest: String? = null
}

private class LoginResponse {
    lateinit var token: String
}

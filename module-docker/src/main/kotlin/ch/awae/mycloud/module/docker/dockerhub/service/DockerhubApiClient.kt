package ch.awae.mycloud.module.docker.dockerhub.service

import ch.awae.mycloud.common.*
import ch.awae.mycloud.module.docker.dockerhub.*
import com.fasterxml.jackson.annotation.*
import com.github.benmanes.caffeine.cache.*
import org.springframework.boot.web.client.*
import org.springframework.stereotype.*
import org.springframework.web.client.*
import java.time.*
import kotlin.time.Duration.Companion.minutes

private typealias ResourceID = Pair<String?, String>
private typealias ResourceState = Map<String, List<Tag>>

@Repository
class DockerhubApiClient(private val dockerProperties: DockerhubProperties) {

    private val logger = createLogger()

    private val http = ExpiringInstance(9.minutes, ::buildRestTemplate)
    private val apiUrl = dockerProperties.apiUrl

    private val cache: LoadingCache<ResourceID, ResourceState> = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(10))
        .build { (namespace, repository) -> doGetTagList(namespace, repository) }

    fun getTagList(namespace: String?, repository: String): Map<String, List<Tag>> {
        return cache.get(Pair(namespace, repository))
    }

    private fun doGetTagList(namespace: String?, repository: String): Map<String, List<Tag>> {
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

        val response = http().getForObject(url, TagListResponse::class.java)!!
        return fetchTags(response.next, previousResults + response.results, invocationCounter + 1)
    }

    private fun buildRestTemplate(): RestTemplate {
        logger.info("creating restTemplate for dockerhub API")
        val httpBuilder = RestTemplateBuilder().defaultHeader("Content-Type", "application/json")

        // initial request to get an auth token
        val request = mapOf(
            "identifier" to dockerProperties.username,
            "secret" to dockerProperties.password
        )
        val token = httpBuilder.build()
            .postForObject("$apiUrl/auth/token", request, LoginResponse::class.java)?.accessToken
            ?: throw kotlin.IllegalStateException("missing auth token for dockerhub")

        logger.info("successfully logged into dockerhub")

        // attach auth token to template by default
        return httpBuilder.defaultHeader("Authorization", "JWT $token").build()
    }
}

data class Tag(val tag: String, val digest: String)

private data class TagListResponse(val next: String?, val results: List<TagListResult>)
private data class TagListResult(val name: String, val digest: String?)
private data class LoginResponse(@JsonProperty("access_token") val accessToken: String)

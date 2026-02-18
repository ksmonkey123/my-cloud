package ch.awae.mycloud.module.docker.dockerhub.client

import ch.awae.mycloud.common.util.ExpiringInstance
import ch.awae.mycloud.common.util.createLogger
import ch.awae.mycloud.module.docker.dockerhub.DockerhubProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.boot.restclient.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import org.springframework.web.client.postForObject
import kotlin.time.Duration.Companion.minutes

@Service
class DockerhubRestClient(
    private val dockerhubProperties: DockerhubProperties,
) {

    private val logger = createLogger()

    private val http = ExpiringInstance(9.minutes, ::buildRestTemplate)
    private val apiUrl = dockerhubProperties.apiUrl

    fun fetchTags(namespace: String?, repository: String): List<TagListEntry> {
        logger.debug("loading tags for ${namespace ?: "_"}/$repository")
        val entries =
            doFetchTags("$apiUrl/namespaces/${namespace ?: "library"}/repositories/${repository}/tags?page_size=100")
        return entries
    }

    private tailrec fun doFetchTags(
        url: String,
        previousEntries: List<TagListEntry> = emptyList()
    ): List<TagListEntry> {
        // step execution
        val response = http().getForObject<TagListResponse>(url)!!
        val entries = previousEntries + response.results

        // recursion
        return if (response.next != null) {
            doFetchTags(response.next, entries)
        } else {
            // exit condition
            entries
        }
    }

    private fun buildRestTemplate(): RestTemplate {
        logger.debug("creating restTemplate for dockerhub API")
        val httpBuilder = RestTemplateBuilder().defaultHeader("Content-Type", "application/json")

        // initial request to get an auth token
        val request = mapOf(
            "identifier" to dockerhubProperties.username,
            "secret" to dockerhubProperties.password,
        )
        val token = httpBuilder.build().postForObject<LoginResponse>("$apiUrl/auth/token", request)?.accessToken
            ?: throw kotlin.IllegalStateException("missing auth token for dockerhub")

        logger.debug("successfully logged into dockerhub")

        // attach auth token to template by default
        return httpBuilder.defaultHeader("Authorization", "JWT $token").build()
    }

}

data class TagListEntry(val name: String, val digest: String?)
private data class TagListResponse(val next: String?, val results: List<TagListEntry>)
private data class LoginResponse(@param:JsonProperty("access_token") val accessToken: String)

package ch.awae.mycloud.module.docker.dockerhub.service

import ch.awae.mycloud.common.util.createLogger
import ch.awae.mycloud.module.docker.dockerhub.client.DockerhubRestClient
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.springframework.stereotype.Repository
import java.time.Duration

private typealias ResourceID = Pair<String?, String>
private typealias ResourceState = Map<String, List<Tag>>

@Repository
class DockerhubApiClient(
    private val dockerhubRestClient: DockerhubRestClient,
) {

    private val logger = createLogger()

    private val cache: LoadingCache<ResourceID, ResourceState> = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(10))
        .build { (namespace, repository) -> doGetTagList(namespace, repository) }

    fun getTagList(namespace: String?, repository: String): Map<String, List<Tag>> {
        return cache.get(Pair(namespace, repository))
    }

    private fun doGetTagList(namespace: String?, repository: String): Map<String, List<Tag>> {
        logger.debug("loading tags for {}/{}", namespace ?: "_", repository)

        val digests = dockerhubRestClient
            .fetchTags(namespace, repository)
            .filter { it.digest != null }
            .map { Tag(it.name, it.digest!!) }
            .groupBy { it.digest }

        logger.debug("found {} digests", digests.size)
        return digests
    }

}

data class Tag(val tag: String, val digest: String)

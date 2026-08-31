package ch.awae.mycloud.module.canary.web.client

import ch.awae.mycloud.common.util.createLogger
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@Component
class ScanningClientImpl(
    private val http: RestTemplate,
) : ScanningClient {

    private val logger = createLogger()

    override fun performScan(url: String, tests: Set<String>): ScanResult {
        try {
            logger.debug("fetching site '{}' for tests {}", url, tests)
            val response = http.getForObject<String>(url)!!
            val failedTests = tests.filter { !response.contains(it) }

            return if (failedTests.isEmpty()) {
                logger.debug("all tests passed")
                ScanResult.Success
            } else {
                logger.warn("canary: some tests failed for site $url: $failedTests")
                ScanResult.Failure(failedTests.toSet())
            }
        } catch (e: Throwable) {
            logger.error("canary: scan error for site $url", e)
            return ScanResult.Error(e)
        }
    }

}
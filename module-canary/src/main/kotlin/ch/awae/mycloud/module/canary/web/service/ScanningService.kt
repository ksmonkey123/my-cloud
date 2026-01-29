package ch.awae.mycloud.module.canary.web.service

import ch.awae.mycloud.api.auth.*
import ch.awae.mycloud.api.email.*
import ch.awae.mycloud.common.*
import ch.awae.mycloud.module.canary.web.model.*
import org.springframework.beans.factory.annotation.*
import org.springframework.data.repository.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*
import org.springframework.web.client.*

@Service
@Transactional
class ScanningService(
    private val monitoredSiteRepository: MonitoredSiteRepository,
    private val testRecordRepository: TestRecordRepository,
    private val http: RestTemplate,
    private val userInfoService: UserInfoService,
    private val emailSendService: EmailSendService,
) {

    private val logger = createLogger()

    fun scan(id: Long) {
        val site = monitoredSiteRepository.findByIdOrNull(id) ?: return
        val lastRecord = testRecordRepository.findLastRecordBySite(site)
        val scanRecord = doScan(site)

        val email = userInfoService.getUserInfo(site.owner)?.email ?: return

        if (lastRecord?.result != TestResult.SUCCESS && scanRecord.result != TestResult.SUCCESS) {
            // at least 2 failures in series -> alert
            logger.warn("sequential failures for $id")
            sendFailure(email, lastRecord, scanRecord)
        } else if (lastRecord != null && lastRecord.result != TestResult.SUCCESS) {
            // previous error resolved
            logger.info("previous failure resolved for $id")
            sendResolved(email, lastRecord)
        }
    }

    fun sendFailure(email: String, lastRecord: TestRecord?, currentRecord: TestRecord) {
        val message = "Website scan failed!\n\n" +
                "URL: <${currentRecord.site.siteUrl}>\nText:" +
                currentRecord.failedTests.fold("") { acc, s -> "$acc\n * $s" }
        emailSendService.send(
            EmailMessage(
                recipient = email,
                subject = "website canary test failed(${currentRecord.result})",
                body = EmailMessage.MarkdownBody(message),
            )
        )
    }

    fun sendResolved(email: String, lastRecord: TestRecord) {
        // TODO: resolution message
    }

    private fun doScan(site: MonitoredSite): TestRecord {
        val record = try {
            val tests = site.tests.filterValues { it }.keys
            logger.info("fetching site ${site.id}: '${site.siteUrl}' with tests $tests")
            val response = http.getForObject(site.siteUrl, String::class.java)!!
            val failedTests = tests.filter { !response.contains(it) }

            if (failedTests.isEmpty()) {
                logger.info("all tests passed")
                TestRecord(site, TestResult.SUCCESS, null, mutableListOf())
            } else {
                logger.warn("some tests failed: $failedTests")
                TestRecord(site, TestResult.FAILURE, null, failedTests.toMutableList())
            }
        } catch (e: Throwable) {
            logger.error("test error", e)
            TestRecord(site, TestResult.ERROR, e.message ?: e.toString(), mutableListOf())
        }
        return testRecordRepository.save(record)
    }

}
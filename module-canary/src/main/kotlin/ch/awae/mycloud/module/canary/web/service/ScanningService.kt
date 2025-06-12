package ch.awae.mycloud.module.canary.web.service

import ch.awae.mycloud.common.*
import ch.awae.mycloud.module.canary.MessageSender
import ch.awae.mycloud.module.canary.web.model.MonitoredSite
import ch.awae.mycloud.module.canary.web.model.MonitoredSiteRepository
import ch.awae.mycloud.module.canary.web.model.TestRecord
import ch.awae.mycloud.module.canary.web.model.TestRecordRepository
import ch.awae.mycloud.module.canary.web.model.TestResult
import org.springframework.data.repository.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*
import org.springframework.web.client.*
import kotlin.collections.filter
import kotlin.collections.filterValues
import kotlin.collections.fold
import kotlin.collections.toMutableList
import kotlin.text.contains

@Service
@Transactional
class ScanningService(
    private val monitoredSiteRepository: MonitoredSiteRepository,
    private val testRecordRepository: TestRecordRepository,
    private val messageSender: MessageSender,
    private val http: RestTemplate,
) {

    private val logger = createLogger()

    fun scan(id: Long) {
        val site = monitoredSiteRepository.findByIdOrNull(id) ?: return
        val lastRecord = testRecordRepository.findLastRecordBySite(site)
        val scanRecord = doScan(site)

        if (lastRecord?.result != TestResult.SUCCESS && scanRecord.result != TestResult.SUCCESS) {
            // at least 2 failures in series -> alert
            logger.warn("sequential failures for $id")
            sendFailure(lastRecord, scanRecord)
        } else if (lastRecord != null && lastRecord.result != TestResult.SUCCESS) {
            // previous error resolved
            logger.info("previous failure resolved for $id")
            sendResolved(lastRecord)
        }
    }

    fun sendFailure(lastRecord: TestRecord?, currentRecord: TestRecord) {
        val message = "Website scan failed!\n\n" +
                "URL: ${currentRecord.site.siteUrl}\nText:" +
                currentRecord.failedTests.fold("") { acc, s -> "$acc\n - $s" }
        messageSender.sendMessage("website canary test failed (${currentRecord.result})", message)
    }

    fun sendResolved(lastRecord: TestRecord) {
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
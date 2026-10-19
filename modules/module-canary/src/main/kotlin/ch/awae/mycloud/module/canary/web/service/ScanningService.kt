package ch.awae.mycloud.module.canary.web.service

import ch.awae.mycloud.auth.UserInfoService
import ch.awae.mycloud.common.util.createLogger
import ch.awae.mycloud.email.EmailMessage
import ch.awae.mycloud.email.EmailSendService
import ch.awae.mycloud.module.canary.web.client.ScanResult
import ch.awae.mycloud.module.canary.web.client.ScanningClient
import ch.awae.mycloud.module.canary.web.model.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ScanningService(
    private val monitoredSiteRepository: MonitoredSiteRepository,
    private val testRecordRepository: TestRecordRepository,
    private val userInfoService: UserInfoService,
    private val emailSendService: EmailSendService,
    private val scanningClient: ScanningClient,
    @param:Value("\${canary.consecutive-failure-threshold}")
    private val consecutiveFailureThreshold: Int,
) {

    private val logger = createLogger()

    init {
        logger.info("canary: consecutive failure threshold: $consecutiveFailureThreshold")
    }

    fun scan(id: Long) {
        val site = monitoredSiteRepository.findByIdOrNull(id) ?: return
        val lastRecord = testRecordRepository.findLastRecordBySite(site)
        val scanRecord = doScan(site, lastRecord?.consecutiveFailures ?: 0)

        logger.debug("scan result for {}: {}", site.siteUrl, scanRecord.result)

        val email = userInfoService.getUserInfo(site.owner)?.email ?: return

        when (scanRecord.result) {
            TestResult.FAILURE, TestResult.ERROR -> {
                if (scanRecord.consecutiveFailures >= consecutiveFailureThreshold) {
                    // at least 2 failures in series -> alert
                    logger.warn("sequential failures for $id")
                    sendFailure(email, scanRecord)
                }
            }

            TestResult.SUCCESS -> {
                if (lastRecord != null && lastRecord.consecutiveFailures >= consecutiveFailureThreshold) {
                    // previous error resolved
                    logger.info("previous failure(s) resolved for $id")
                    sendResolved(email, lastRecord)
                }
            }
        }
    }

    fun sendFailure(email: String, testRecord: TestRecord) {
        val message = "Website scan failed!\n\n" +
                "URL: <${testRecord.site.siteUrl}>\nText:" +
                testRecord.failedTests.fold("") { acc, s -> "$acc\n * $s" }
        emailSendService.send(
            EmailMessage(
                recipient = email,
                subject = "website canary test failed(${testRecord.result})",
                body = EmailMessage.MarkdownBody(message),
            )
        )
    }

    fun sendResolved(email: String, previousTestRecord: TestRecord) {
        val message = "Website scan resolved!\n\n" +
                "URL: <${previousTestRecord.site.siteUrl}>\nText:" +
                previousTestRecord.failedTests.fold("") { acc, s -> "$acc\n * $s" }
        emailSendService.send(
            EmailMessage(
                recipient = email,
                subject = "website canary test resolved",
                body = EmailMessage.MarkdownBody(message),
            )
        )
    }

    private fun doScan(site: MonitoredSite, previousFailuresCount: Int): TestRecord {
        val tests = site.tests.filterValues { it }.keys
        val record = when (val result = scanningClient.performScan(site.siteUrl, tests)) {
            is ScanResult.Success -> TestRecord(
                site,
                TestResult.SUCCESS,
                null,
                mutableListOf(),
                0,
            )

            is ScanResult.Error -> TestRecord(
                site,
                TestResult.ERROR,
                result.throwable.message ?: result.throwable.toString(),
                mutableListOf(),
                previousFailuresCount + 1,
            )

            is ScanResult.Failure -> TestRecord(
                site,
                TestResult.FAILURE,
                null,
                result.failedTests.toMutableList(),
                previousFailuresCount + 1,
            )
        }
        return testRecordRepository.save(record)
    }

}
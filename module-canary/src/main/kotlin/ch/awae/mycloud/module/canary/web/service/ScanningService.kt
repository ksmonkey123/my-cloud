package ch.awae.mycloud.module.canary.web.service

import ch.awae.mycloud.auth.UserInfoService
import ch.awae.mycloud.email.EmailMessage
import ch.awae.mycloud.email.EmailSendService
import ch.awae.mycloud.common.util.createLogger
import ch.awae.mycloud.module.canary.web.client.ScanResult
import ch.awae.mycloud.module.canary.web.client.ScanningClient
import ch.awae.mycloud.module.canary.web.model.*
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
        val tests = site.tests.filterValues { it }.keys
        val result = scanningClient.performScan(site.siteUrl, tests)

        val record = when (result) {
            is ScanResult.Success -> TestRecord(
                site,
                TestResult.SUCCESS,
                null,
                mutableListOf(),
            )

            is ScanResult.Error -> TestRecord(
                site,
                TestResult.ERROR,
                result.throwable.message ?: result.throwable.toString(),
                mutableListOf(),
            )

            is ScanResult.Failure -> TestRecord(
                site,
                TestResult.FAILURE,
                null,
                result.failedTests.toMutableList(),
            )
        }
        return testRecordRepository.save(record)
    }

}
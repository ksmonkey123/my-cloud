package ch.awae.mycloud.service.canary.service

import ch.awae.mycloud.*
import ch.awae.mycloud.audit.*
import ch.awae.mycloud.service.canary.dto.*
import ch.awae.mycloud.service.canary.model.*
import org.springframework.data.repository.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class MonitoredSiteService(
    private val monitoredSiteRepository: MonitoredSiteRepository,
    private val testRecordRepository: TestRecordRepository,
) {

    @AuditLog
    fun create(request: MonitoredSiteDto): MonitoredSiteSummaryDto {

        val tests = request.tests.associateBy(
            keySelector = SiteTestDto::testString,
            valueTransform = SiteTestDto::enabled,
        )

        val site = monitoredSiteRepository.save(MonitoredSite(request.url, request.enabled, tests.toMutableMap()))

        return MonitoredSiteSummaryDto(
            site.id,
            site.siteUrl,
            site.enabled,
            site.tests.map { (string, enabled) -> SiteTestDto(string, enabled) },
            null
        )
    }

    @AuditLog
    fun update(id: Long, request: MonitoredSiteDto): MonitoredSiteSummaryDto {
        val entity = monitoredSiteRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("/monitored-site/$id")

        entity.siteUrl = request.url
        entity.enabled = request.enabled

        entity.tests.clear()
        entity.tests.putAll(request.tests.map { it.testString to it.enabled })

        val savedEntity = monitoredSiteRepository.save(entity)

        return MonitoredSiteSummaryDto(
            savedEntity.id,
            savedEntity.siteUrl,
            savedEntity.enabled,
            savedEntity.tests.map { (string, enabled) -> SiteTestDto(string, enabled) },
            testRecordRepository.findLastRecordBySite(savedEntity)?.let {
                TestRecordDto(it.result, it.creationTimestamp)
            },
        )
    }

}
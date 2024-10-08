package ch.awae.mycloud.service.canary.web.service

import ch.awae.mycloud.*
import ch.awae.mycloud.audit.*
import ch.awae.mycloud.service.canary.web.dto.*
import ch.awae.mycloud.service.canary.web.model.*
import jakarta.persistence.*
import org.springframework.data.repository.*
import org.springframework.jdbc.core.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*
import javax.sql.*

@Service
@Transactional
class MonitoredSiteService(
    private val monitoredSiteRepository: MonitoredSiteRepository,
    private val testRecordRepository: TestRecordRepository,
    dataSource: DataSource,
) {

    private val logger = createLogger()
    private val sql = JdbcTemplate(dataSource)

    fun listAll(): List<MonitoredSiteSummaryDto> {
        return monitoredSiteRepository.listSorted().map(::buildSummaryDto)
    }

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

        return buildSummaryDto(savedEntity)
    }

    private fun buildSummaryDto(savedEntity: MonitoredSite): MonitoredSiteSummaryDto {
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

    @AuditLog
    fun delete(id: Long) {
        val site = monitoredSiteRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("/monitored-site/$id")

        /*
          delete logic implemented natively. The element collections and relations in the JPA model lead to a
          massive amount of queries if deletion were to be performed via JPA.
         */
        sql.update("delete from failed_test where record_id in (select id from test_record where site_id = ?)", id)
        val deletedRecords = sql.update("delete from test_record where site_id = ?", id)
        sql.update("delete from site_test where site_id = ?", id)
        val deletedSites = sql.update("delete from monitored_site where id = ? and version = ?", id, site.version)

        if (deletedSites != 1) {
            throw OptimisticLockException("$deletedSites sites were deleted. expected 1")
        }

        logger.info("delted site $id with $deletedRecords test records")
    }
}
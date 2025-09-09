package ch.awae.mycloud.module.canary.web.model

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query
import java.time.*

@Entity
@Table(name = "web_test_record", schema = "canary")
class TestRecord(
    @ManyToOne
    @JoinColumn(updatable = false)
    val site: MonitoredSite,
    @Enumerated(EnumType.STRING)
    @Column(updatable = false)
    val result: TestResult,
    @Column(updatable = false)
    val errorMessage: String?,
    @ElementCollection
    @CollectionTable(schema = "canary", name = "web_failed_test", joinColumns = [JoinColumn(name = "record_id")])
    @Column(name = "test_string")
    val failedTests: MutableList<String>,
    val recordedAt: LocalDateTime = LocalDateTime.now(),
) : IdBaseEntity()

interface TestRecordRepository : JpaRepository<TestRecord, Long> {

    @Query("select record from TestRecord record where record.site = :site order by record.recordedAt desc limit 1")
    fun findLastRecordBySite(site: MonitoredSite) : TestRecord?

}
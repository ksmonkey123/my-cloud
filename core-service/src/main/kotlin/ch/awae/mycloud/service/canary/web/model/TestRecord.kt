package ch.awae.mycloud.service.canary.web.model

import ch.awae.mycloud.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query

@Entity
@Table(name = "canary_web_test_record")
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
    @CollectionTable(name = "canary_web_failed_test", joinColumns = [JoinColumn(name = "record_id")])
    @Column(name = "test_string")
    val failedTests: MutableList<String>
) : IdBaseEntity()

interface TestRecordRepository : JpaRepository<TestRecord, Long> {

    @Query("select record from TestRecord record where record.site = :site order by record._creationTimestamp desc limit 1")
    fun findLastRecordBySite(site: MonitoredSite) : TestRecord?

}
package ch.awae.mycloud.service.canary.model

import ch.awae.mycloud.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

@Entity
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
    @CollectionTable(name = "failed_test", joinColumns = [JoinColumn(name = "record_id")])
    @Column(name = "test_string")
    val failedTests: MutableList<String>
) : IdBaseEntity()

interface TestRecordRepository : JpaRepository<TestRecord, Long> {

    @Query("select record from TestRecord record where record.site = :site order by record._creationTimestamp desc limit 1")
    fun findLastRecordBySite(site: MonitoredSite) : TestRecord?

}
package ch.awae.mycloud.service.canary.web.model

import ch.awae.mycloud.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query

@Entity
class MonitoredSite(
    var siteUrl: String,
    var enabled: Boolean,
    @ElementCollection
    @CollectionTable(
        name = "site_test",
        joinColumns = [JoinColumn(name = "site_id")],
    )
    @MapKeyColumn(name = "test_string")
    @Column(name = "enabled")
    val tests: MutableMap<String, Boolean>,
) : IdBaseEntity() {

    @OneToMany(mappedBy = "site")
    @OrderBy("_creationTimestamp DESC")
    val records = listOf<TestRecord>()
}

interface MonitoredSiteRepository : JpaRepository<MonitoredSite, Long> {

    @Query("select site from MonitoredSite site order by site.id asc")
    fun listSorted(): List<MonitoredSite>

    @Query("select site.id from MonitoredSite site where site.enabled")
    fun findEnabledIds(): List<Long>

}
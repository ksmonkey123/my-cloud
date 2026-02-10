package ch.awae.mycloud.module.canary.web.model

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query

@Entity(name = "canary_MonitoredSite")
@Table(name = "web_monitored_site", schema = "canary")
class MonitoredSite(
    @Column(updatable = false, nullable = false)
    val owner: String,
    @Column(nullable = false)
    var siteUrl: String,
    var enabled: Boolean,
    @ElementCollection
    @CollectionTable(
        schema = "canary",
        name = "web_site_test",
        joinColumns = [JoinColumn(name = "site_id")],
    )
    @MapKeyColumn(name = "test_string")
    @Column(name = "enabled", nullable = false)
    val tests: MutableMap<String, Boolean>,
) : IdBaseEntity()

interface MonitoredSiteRepository : JpaRepository<MonitoredSite, Long> {

    @Query("select site.id from canary_MonitoredSite site where site.enabled")
    fun findEnabledIds(): List<Long>

}
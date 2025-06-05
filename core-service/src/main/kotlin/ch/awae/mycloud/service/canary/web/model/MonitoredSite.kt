package ch.awae.mycloud.service.canary.web.model

import ch.awae.mycloud.common.db.IdBaseEntity
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query

@Entity
@Table(name = "canary_web_monitored_site")
class MonitoredSite(
    var siteUrl: String,
    var enabled: Boolean,
    @ElementCollection
    @CollectionTable(
        name = "canary_web_site_test",
        joinColumns = [JoinColumn(name = "site_id")],
    )
    @MapKeyColumn(name = "test_string")
    @Column(name = "enabled")
    val tests: MutableMap<String, Boolean>,
) : IdBaseEntity()

interface MonitoredSiteRepository : JpaRepository<MonitoredSite, Long> {

    @Query("select site.id from MonitoredSite site where site.enabled")
    fun findEnabledIds(): List<Long>

}
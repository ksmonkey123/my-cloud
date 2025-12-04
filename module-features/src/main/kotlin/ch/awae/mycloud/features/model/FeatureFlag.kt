package ch.awae.mycloud.features.model

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query

@Entity
@Table(name = "feature_flag", schema = "features")
class FeatureFlag(
    @Id
    @Column(updatable = false)
    val id: String,
    var enabled: Boolean,
) : BaseEntity()

interface FeatureFlagRepository : JpaRepository<FeatureFlag, String> {

    @Query("select f from FeatureFlag f order by f.id asc")
    fun listAllSorted(): List<FeatureFlag>

    @Query("select f from FeatureFlag f where f.enabled = :enabled order by f.id asc")
    fun listByEnabled(enabled: Boolean): List<FeatureFlag>

}
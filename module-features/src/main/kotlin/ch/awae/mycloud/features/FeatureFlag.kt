package ch.awae.mycloud.features

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*

@Entity
@Table(name = "feature_flag", schema = "features")
class FeatureFlag(
    @Id
    @Column(updatable = false)
    val id: String,
    var enabled: Boolean,
) : BaseEntity()

interface FeatureFlagRepository : JpaRepository<FeatureFlag, String>
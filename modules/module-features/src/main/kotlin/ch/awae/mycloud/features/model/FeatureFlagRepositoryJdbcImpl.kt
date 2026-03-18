package ch.awae.mycloud.features.model

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class FeatureFlagRepositoryJdbcImpl(private val sql: NamedParameterJdbcTemplate) : FeatureFlagRepository {

    override fun existsById(id: String): Boolean {
        val count = sql.queryForObject(
            "select count(*) from features.feature_flag where id = :id",
            mapOf("id" to id),
            Long::class.java
        )!!

        return count > 0
    }

    override fun getState(feature: String): Boolean? {
        return sql.queryForList(
            "select enabled from features.feature_flag where id = :id",
            mapOf("id" to feature),
            Boolean::class.java
        ).firstOrNull()
    }

    override fun setState(feature: String, state: Boolean) {
        sql.update(
            """
            insert into features.feature_flag (id, enabled)
            values (:id, :enabled)
            on conflict (id) do update
                set enabled = excluded.enabled
            where features.feature_flag.enabled <> excluded.enabled
            """.trimIndent(), mapOf(
                "id" to feature,
                "enabled" to state,
            )
        )
    }

    override fun delete(feature: String) {
        sql.update("delete from features.feature_flag where id = :id", mapOf("id" to feature))
    }

    override fun list(state: Boolean?): List<Pair<String, Boolean>> {
        return if (state == null) {
            sql.query("select id, enabled from features.feature_flag order by id") { rs, _ ->
                Pair(rs.getString("id"), rs.getBoolean("enabled"))
            }
        } else {
            sql.query(
                "select id, enabled from features.feature_flag where enabled = :state order by id",
                mapOf("state" to state)
            ) { rs, _ ->
                Pair(rs.getString("id"), rs.getBoolean("enabled"))
            }
        }
    }

}
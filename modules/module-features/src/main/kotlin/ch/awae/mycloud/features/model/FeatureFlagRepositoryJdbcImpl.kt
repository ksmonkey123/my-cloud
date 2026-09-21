package ch.awae.mycloud.features.model

import ch.awae.mycloud.common.db.singleOrNull
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

@Repository
class FeatureFlagRepositoryJdbcImpl(private val db: JdbcClient) : FeatureFlagRepository {

    override fun existsById(id: String): Boolean {
        val count = db
            .sql("select count(*) from features.feature_flag where id = :id")
            .param("id", id)
            .query(Long::class.java)
            .single()

        return count > 0
    }

    override fun getState(feature: String): Boolean? {
        return db
            .sql("select enabled from features.feature_flag where id = :id")
            .param("id", feature)
            .query(Boolean::class.java)
            .singleOrNull()
    }

    override fun setState(feature: String, state: Boolean) {
        db
            .sql(
                """
            insert into features.feature_flag (id, enabled)
            values (:id, :enabled)
            on conflict (id) do update
                set enabled = excluded.enabled
            where features.feature_flag.enabled <> excluded.enabled
            """.trimIndent()
            )
            .param("id", feature)
            .param("enabled", state)
            .update()
    }

    override fun delete(feature: String) {
        db.sql("delete from features.feature_flag where id = :id").param("id", feature).update()
    }

    override fun list(state: Boolean?): List<Pair<String, Boolean>> {
        val statement = if (state == null) {
            db.sql("select id, enabled from features.feature_flag order by id")
        } else {
            db.sql("select id, enabled from features.feature_flag where enabled = :state order by id")
                .param("state", state)
        }

        return statement.query { rs, _ -> Pair(rs.getString("id"), rs.getBoolean("enabled")) }.list()
    }
}

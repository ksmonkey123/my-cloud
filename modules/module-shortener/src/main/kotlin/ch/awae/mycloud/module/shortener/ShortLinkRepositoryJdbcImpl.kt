package ch.awae.mycloud.module.shortener

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class ShortLinkRepositoryJdbcImpl(private val sql: NamedParameterJdbcTemplate) : ShortLinkRepository {

    override fun existsById(id: String): Boolean {
        val count = sql.queryForObject(
            "select count(*) from shortener.link where id = :id",
            mapOf("id" to id),
            Long::class.java
        )!!
        return count > 0
    }

    override fun deleteByIdAndUsername(id: String, username: String): Boolean {
        val deleted = sql.update(
            "delete from shortener.link where id = :id and username = :username",
            mapOf(
                "id" to id,
                "username" to username,
            )
        )
        return deleted > 0
    }

    override fun findById(id: String): ShortLink? {
        val result = sql.query(
            "select id, target_url, username from shortener.link where id = :id",
            mapOf("id" to id)
        ) { rs, _ ->
            ShortLink(
                id = rs.getString("id"),
                targetUrl = rs.getString("target_url"),
                username = rs.getString("username"),
            )
        }
        return result.firstOrNull()
    }

    override fun findByUsername(username: String): List<ShortLink> {
        return sql.query(
            "select id, target_url, username from shortener.link where username = :username",
            mapOf("username" to username),
        ) { rs, _ ->
            ShortLink(
                id = rs.getString("id"),
                targetUrl = rs.getString("target_url"),
                username = rs.getString("username"),
            )
        }
    }

    override fun save(shortLink: ShortLink) {
        sql.update(
            "insert into shortener.link (id, target_url, username) values (:id, :target_url, :username)",
            mapOf(
                "id" to shortLink.id,
                "target_url" to shortLink.targetUrl,
                "username" to shortLink.username,
            )
        )
    }

}
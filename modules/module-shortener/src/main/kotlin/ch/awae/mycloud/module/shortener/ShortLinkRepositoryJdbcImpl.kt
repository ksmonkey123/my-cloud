package ch.awae.mycloud.module.shortener

import ch.awae.mycloud.common.db.singleOrNull
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

@Repository
class ShortLinkRepositoryJdbcImpl(private val db: JdbcClient) : ShortLinkRepository {

    override fun existsById(id: String): Boolean {
        val count = db
            .sql("select count(*) from shortener.link where id = :id")
            .param("id", id)
            .query(Long::class.java)
            .single()
        return count > 0
    }

    override fun deleteByIdAndUsername(id: String, username: String): Boolean {
        val deleted = db
            .sql("delete from shortener.link where id = :id and username = :username")
            .param("id", id)
            .param("username", username)
            .update()
        return deleted > 0
    }

    override fun findById(id: String): ShortLink? {
        return db
            .sql("select id, target_url, username from shortener.link where id = :id")
            .param("id", id)
            .query { rs, _ ->
                ShortLink(
                    id = rs.getString("id"),
                    targetUrl = rs.getString("target_url"),
                    username = rs.getString("username"),
                )
            }
            .singleOrNull()
    }

    override fun findByUsername(username: String): List<ShortLink> {
        return db
            .sql("select id, target_url, username from shortener.link where username = :username")
            .param("username", username)
            .query { rs, _ ->
                ShortLink(
                    id = rs.getString("id"),
                    targetUrl = rs.getString("target_url"),
                    username = rs.getString("username"),
                )
            }
            .list()
    }

    override fun save(shortLink: ShortLink) {
        db
            .sql("insert into shortener.link (id, target_url, username) values (:id, :target_url, :username)")
            .param("id", shortLink.id)
            .param("target_url", shortLink.targetUrl)
            .param("username", shortLink.username)
            .update()
    }

}
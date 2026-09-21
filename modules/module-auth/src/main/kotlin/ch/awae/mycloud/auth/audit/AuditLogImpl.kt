package ch.awae.mycloud.auth.audit

import ch.awae.mycloud.auth.RequestContext
import ch.awae.mycloud.common.util.GUID
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.Instant

@Repository
class AuditLogImpl(private val db: JdbcClient) : AuditLog {

    override fun recordAccess(accountId: Long, requestContext: RequestContext, tokenId: Long?, keyId: Long?) {
        db.sql("INSERT INTO auth.audit_log (id, account_id, method, path, token_id, key_id, created_at) VALUES (:id, :accountId, :method, :path, :tokenId, :keyId, :createdAt)")
            .param("id", GUID.generateV7())
            .param("accountId", accountId)
            .param("method", requestContext.requestMethod)
            .param("path", requestContext.requestPath)
            .param("tokenId", tokenId)
            .param("keyId", keyId)
            .param("createdAt", Timestamp.from(Instant.now()))
            .update()
    }


}
package ch.awae.mycloud.auth.audit

import ch.awae.mycloud.auth.RequestContext
import ch.awae.mycloud.common.util.GUID
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.Instant

@Repository
class AuditLogImpl(private val sql: NamedParameterJdbcTemplate) : AuditLog {

    override fun recordAccess(accountId: Long, requestContext: RequestContext, tokenId: Long?, keyId: Long?) {
        sql.update(
            "INSERT INTO auth.audit_log (id, account_id, method, path, token_id, key_id, created_at) VALUES (:id, :accountId, :method, :path, :tokenId, :keyId, :createdAt)",
            mapOf(
                "id" to GUID.generateV7(),
                "accountId" to accountId,
                "method" to requestContext.requestMethod,
                "path" to requestContext.requestPath,
                "tokenId" to tokenId,
                "keyId" to keyId,
                "createdAt" to Timestamp.from(Instant.now()),
            )
        )
    }


}
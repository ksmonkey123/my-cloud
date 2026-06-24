package ch.awae.mycloud.auth.audit

import ch.awae.mycloud.auth.RequestContext

interface AuditLog {

    /**
     * Records an access event for the given account and request context, optionally associating
     * it with a specific token or key.
     *
     * @param accountId The database ID of the account accessing the resource.
     * @param requestContext The context of the request, including details such as method and path.
     * @param tokenId The database ID of the token, if applicable. Can be null.
     * @param keyId The database ID of the key, if applicable. Can be null.
     */
    fun recordAccess(
        accountId: Long,
        requestContext: RequestContext,
        tokenId: Long? = null,
        keyId: Long? = null,
    )

}
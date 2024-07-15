package ch.awae.mycloud.audit

import ch.awae.mycloud.auth.*
import java.sql.*

data class AuditLogEntry(
    val traceId: String,
    val service: String,
    // timing information
    val timing: Timing,
    // security information
    val username: String? = AuthInfo.username,
    // rest request information
    val request: Request? = null,
    // method information
    val method: Method,
) {
    data class Timing(val start: Timestamp, val end: Timestamp)

    data class Request(val verb: String, val path: String)

    data class Method(
        val component: String,
        val method: String,
        val parameters: Map<String, String>,
        val error: String?,
    )
}
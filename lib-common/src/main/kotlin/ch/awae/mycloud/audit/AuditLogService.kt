package ch.awae.mycloud.audit

import ch.awae.mycloud.auth.*
import org.springframework.beans.factory.annotation.*
import org.springframework.stereotype.*
import java.lang.reflect.*
import java.sql.*
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.*

@Service
class AuditLogService(
    private val kafkaSender: KafkaSender,
    @Value("\${spring.application.name}") val serviceName: String,
) {

    fun recordMethodCall(
        method: Method,
        args: Array<Any?>,
        error: Throwable?,
    ) {
        val params: Map<String, String> = if (args.isNotEmpty()) {
            val paramInfo = method.kotlinFunction?.valueParameters
            if (paramInfo == null) {
                emptyMap()
            } else {
                args.mapIndexedNotNull { index, arg -> mapParameter(index, paramInfo[index], arg) }
                    .associate { p -> p }
            }
        } else {
            emptyMap()
        }

        val request = TraceInformation.requestInfo?.let { AuditLogEntry.Request(it.verb, it.path) }

        val entry = AuditLogEntry(
            TraceInformation.traceId!!,
            serviceName,
            AuditLogEntry.Timing(
                TraceInformation.startTimestamp!!,
                Timestamp(System.currentTimeMillis()),
            ),
            AuthInfo.username,
            request,
            AuditLogEntry.Method(
                method.declaringClass.simpleName,
                method.name,
                params,
                error?.toString()
            )
        )

        kafkaSender.send(entry)
    }

    private fun mapParameter(index: Int, parameter: KParameter, arg: Any?): Pair<String, String>? {
        val key = parameter.name ?: "arg#$index"
        return when {
            arg == null -> null
            parameter.hasAnnotation<NoAudit>() -> null
            parameter.hasAnnotation<RedactedAudit>() -> Pair(key, "<redacted>")
            else -> Pair(key, arg.toString())
        }
    }

}
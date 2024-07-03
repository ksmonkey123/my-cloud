package ch.awae.mycloud.audit

import ch.awae.mycloud.*
import java.sql.*
import java.time.*
import java.util.*
import kotlin.random.Random

object TraceInformation {

    private val base64Encoder = Base64.getUrlEncoder()
    private val traceIdHolder = ThreadLocal<TraceIdLayer?>()
    private val requestHolder = ThreadLocal<RequestInformation?>()

    val traceId: String?
        get() = traceIdHolder.get()?.toString()

    val startTimestamp: Timestamp?
        get() = traceIdHolder.get()?.startTimestamp

    val requestInfo: RequestInformation?
        get() = requestHolder.get()

    fun init(externalTraceId: String? = null, requestInformation: RequestInformation? = null) {
        val traceId = if (externalTraceId != null) {
            externalTraceId + "/" + generateId()
        } else {
            generateId()
        }
        traceIdHolder.set(TraceIdLayer(traceId, null))
        requestHolder.set(requestInformation)
    }

    fun push() {
        traceIdHolder.update { current -> current!!.nextChild() }
    }

    fun pop() {
        traceIdHolder.update { current -> current?.parent }
    }

    private fun generateId(): String {
        return base64Encoder.encodeToString(Random.nextBytes(9))
    }

    data class RequestInformation(
        val path: String,
        val verb: String,
    )

    private class TraceIdLayer(
        val id: String,
        val parent: TraceIdLayer?,
    ) {
        var childCounter = 0
        val startTimestamp: Timestamp = Timestamp.from(Instant.now())

        override fun toString(): String {
            return if (parent != null) {
                "$parent$$id"
            } else {
                id
            }
        }

        fun nextChild(): TraceIdLayer {
            childCounter++
            return TraceIdLayer(childCounter.toString(16).padStart(2, '0'), this)
        }
    }

}
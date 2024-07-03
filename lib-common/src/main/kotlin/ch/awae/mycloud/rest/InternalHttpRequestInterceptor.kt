package ch.awae.mycloud.rest

import ch.awae.mycloud.audit.*
import ch.awae.mycloud.auth.*
import org.springframework.http.*
import org.springframework.http.client.*

class InternalHttpRequestInterceptor : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        // attach bearer token if available
        (AuthInfo.info as? TokenBackedAuthInfo)?.token?.let { request.headers.setBearerAuth(it) }
        TraceInformation.traceId?.let { request.headers.set("TraceID", it) }
        // continue request
        return execution.execute(request, body)
    }
}
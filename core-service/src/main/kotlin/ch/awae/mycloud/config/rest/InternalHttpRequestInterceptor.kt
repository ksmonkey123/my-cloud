package ch.awae.mycloud.config.rest

import ch.awae.mycloud.common.auth.AuthInfo
import ch.awae.mycloud.common.auth.TokenBackedAuthInfo
import org.springframework.http.*
import org.springframework.http.client.*

class InternalHttpRequestInterceptor : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        // attach bearer token if available
        (AuthInfo.Companion.info as? TokenBackedAuthInfo)?.token?.let { request.headers.setBearerAuth(it) }
        // continue request
        return execution.execute(request, body)
    }
}
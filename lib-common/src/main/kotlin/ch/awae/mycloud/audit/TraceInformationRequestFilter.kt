package ch.awae.mycloud.audit

import jakarta.servlet.*
import jakarta.servlet.http.*
import org.springframework.stereotype.*
import org.springframework.web.filter.*

@Component
class TraceInformationRequestFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val traceId: String? = request.getHeader("TraceID")
        TraceInformation.init(traceId, TraceInformation.RequestInformation(request.servletPath, request.method))
        filterChain.doFilter(request, response)
    }

}
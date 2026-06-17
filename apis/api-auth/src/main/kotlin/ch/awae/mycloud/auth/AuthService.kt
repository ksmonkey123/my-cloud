package ch.awae.mycloud.auth

interface AuthService {
    fun authenticateToken(tokenString: String, context: RequestContext): AuthInfo?
}

data class RequestContext(
    val requestMethod : String,
    val requestPath: String,
)
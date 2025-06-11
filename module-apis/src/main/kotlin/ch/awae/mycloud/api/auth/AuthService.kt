package ch.awae.mycloud.api.auth

interface AuthService {
    fun authenticateToken(tokenString: String): AuthInfo?
}
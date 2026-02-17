package ch.awae.mycloud.auth

interface AuthService {
    fun authenticateToken(tokenString: String): AuthInfo?
}
package ch.awae.mycloud.auth

@Suppress("kotlin:S6517")
interface UserInfoService {

    fun getUserInfo(username: String) : UserInfo?

}
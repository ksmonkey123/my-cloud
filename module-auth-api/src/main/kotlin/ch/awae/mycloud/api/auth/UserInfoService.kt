package ch.awae.mycloud.api.auth

interface UserInfoService {

    fun getUserInfo(username: String) : UserInfo?

}
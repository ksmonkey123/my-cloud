package ch.awae.mycloud.auth

interface UserInfoService {

    fun getUserInfo(username: String) : UserInfo?

}
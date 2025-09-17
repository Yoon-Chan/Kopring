package com.chan.banklecture.interfaces

interface OauthServiceInterface {
    val provideName: String
    fun getToken(code: String) : Oauth2TokenResponse
    fun getUserInfo(accessToken: String): Oauth2UerResponse
}

interface Oauth2TokenResponse {
    val accessToken: String
}

interface Oauth2UerResponse {
    val id: String
    val email: String?
    val name: String?
}
package com.chan.banklecture.domains.auth.service

import com.chan.banklecture.common.exception.CustomException
import com.chan.banklecture.common.exception.ErrorCode
import com.chan.banklecture.common.httpclient.CallClient
import com.chan.banklecture.config.OAuth2Config
import com.chan.banklecture.interfaces.Oauth2TokenResponse
import com.chan.banklecture.interfaces.Oauth2UerResponse
import com.chan.banklecture.interfaces.OauthServiceInterface
import okhttp3.FormBody
import org.springframework.stereotype.Service

private const val key = "github"

@Service(key)
class GithubAuthService(
    private val config: OAuth2Config,
    private val httpClient: CallClient
): OauthServiceInterface {
    private val oAuthInfo = config.providers[key] ?: throw CustomException(ErrorCode.AUTH_CONFIG_NOT_FOUND, key)
    private val tokenURL = "https://github.com/login/oauth/access_token"
    private val userInfoURL = "https://api.github.com/user"
    override val provideName: String
        get() = key

    override fun getToken(code: String): Oauth2TokenResponse {
        val body = FormBody.Builder()
            .add("code", code)
            .add("client_id", oAuthInfo.clientId)
            .add("client_secret", oAuthInfo.clientSecret)
            .add("redirect_uri", oAuthInfo.redirectUri)
            .add("grant_type", "authorization_code")
            .build()

        val headers = mapOf("Accept" to "application/json")
        val jsonString = httpClient.POST("", headers, body)

    }

    override fun getUserInfo(accessToken: String): Oauth2UerResponse {

    }
}
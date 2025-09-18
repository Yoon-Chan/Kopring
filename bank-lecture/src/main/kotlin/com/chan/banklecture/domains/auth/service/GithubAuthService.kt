package com.chan.banklecture.domains.auth.service

import com.chan.banklecture.common.exception.CustomException
import com.chan.banklecture.common.exception.ErrorCode
import com.chan.banklecture.common.httpclient.CallClient
import com.chan.banklecture.common.json.JsonUtil
import com.chan.banklecture.config.OAuth2Config
import com.chan.banklecture.interfaces.Oauth2TokenResponse
import com.chan.banklecture.interfaces.Oauth2UerResponse
import com.chan.banklecture.interfaces.OauthServiceInterface
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
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
        return JsonUtil.fromJson(jsonString, GithubTokenResponse.serializer())
    }

    override fun getUserInfo(accessToken: String): Oauth2UerResponse {
        val headers = mapOf(
            "Content-Type" to "application/json",
            "Authorization" to "Bearer $accessToken"
        )

        val jsonString = httpClient.GET(userInfoURL, headers)
        val response = JsonUtil.fromJson(jsonString, GithubUserResponseTemp.serializer())
        return response.toOauth2UserResponse()
    }
}

@Serializable
data class GithubTokenResponse(
    @SerialName("access_token") override val accessToken: String
): Oauth2TokenResponse

@Serializable
data class GithubUserResponseTemp(
    val id: Int,
    @SerialName("repos_url") val reposUrl: String,
    val name: String,
) {
    fun toOauth2UserResponse(): GithubUserResponse {
        return GithubUserResponse(
            id = id.toString(),
            email = reposUrl,
            name = name,
        )
    }
}

@Serializable
data class GithubUserResponse(
    override val id: String,
    override val email: String?,
    override val name: String?
): Oauth2UerResponse
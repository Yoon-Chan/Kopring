package com.chan.banklecture.domains.auth.service

import com.chan.banklecture.config.OAuth2Config
import com.chan.banklecture.interfaces.Oauth2TokenResponse
import com.chan.banklecture.interfaces.Oauth2UerResponse
import com.chan.banklecture.interfaces.OauthServiceInterface
import org.springframework.stereotype.Service

private const val key = "google"

@Service(key)
class GoogleAuthService(
    private val config: OAuth2Config
): OauthServiceInterface {
    private val oAuthInfo = config.providers[key] ?: throw TODO("custom Exception")
    override val provideName: String
        get() = key

    override fun getToken(code: String): Oauth2TokenResponse {

    }

    override fun getUserInfo(accessToken: String): Oauth2UerResponse {

    }
}
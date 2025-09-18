package com.chan.banklecture.domains.auth.service

import com.chan.banklecture.common.exception.CustomException
import com.chan.banklecture.common.exception.ErrorCode
import com.chan.banklecture.common.jwt.JwtProvider
import com.chan.banklecture.interfaces.OauthServiceInterface
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val oAuth2ServiceInterface: Map<String, OauthServiceInterface>,
    private val jwtProvider: JwtProvider
) {
    fun handleAuth(state: String, code: String): String {
        //GOOGLE -> google
        val provider = state.lowercase()

        val callService = oAuth2ServiceInterface[provider] ?: throw CustomException(ErrorCode.PROVIDER_NOT_FOUND, provider)
        val accessToken = callService.getToken(code)
        val userInfo = callService.getUserInfo(accessToken.accessToken)
        val token = jwtProvider.createToken(provider, userInfo.email, userInfo.name, userInfo.id)

        //userInfo

    }
}
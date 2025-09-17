package com.chan.banklecture.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/*
    oauth2:
        providers:
            google:
                client-id: ...
                client-secret: ...
                redirect-uri :...
            github
                client-id:...
                ...
*
**/
@Configuration
@ConfigurationProperties("oauth2")
class OAuth2Config {
    val providers: MutableMap<String, Oauth2ProviderValues> = mutableMapOf()
}

data class Oauth2ProviderValues(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
)
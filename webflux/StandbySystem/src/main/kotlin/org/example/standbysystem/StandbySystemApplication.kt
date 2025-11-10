package org.example.standbysystem

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import org.springframework.web.util.UriComponentsBuilder

@SpringBootApplication
@Controller
class StandbySystemApplication {
    val restTemplate = RestTemplate()

    @GetMapping("/")
    fun index(
        @RequestParam(name = "queue", defaultValue = "default") queue: String,
        @RequestParam(name = "user_id") userId: Long,
        request: HttpServletRequest,
    ): String {
        val cookies = request.cookies
        val cookieName = "user-queue-$queue-token"
        val redirectUrl = "http://127.0.0.1:9000/?user_id=$userId"
        println("cookie: $cookies")
        val token = cookies?.firstOrNull { it.name == cookieName }?.value ?: Cookie(cookieName, "").value
        println("token: $token")
        val uri = UriComponentsBuilder
            .fromUriString("http://127.0.0.1:9010")
            .path("/api/v1/queue/allowed")
            .queryParam("user_id", userId)
            .queryParam("queue", queue)
            .queryParam("token", token)
            .encode()
            .build()
            .toUri()

        val response: ResponseEntity<AllowedUserResponse> = restTemplate.getForEntity(uri, AllowedUserResponse::class.java)
        if(response.body?.allowed != true) {
            //대기 웹페이지 리다이렉트
            return "redirect:http://127.0.0.1:9010/waiting-room?user_id=$userId&redirect_url=$redirectUrl";
        }

        //허용상태라면 해당 페이지 진입
        return "index"
    }

    data class AllowedUserResponse(val allowed: Boolean)
}

fun main(args: Array<String>) {
    runApplication<StandbySystemApplication>(*args)
}

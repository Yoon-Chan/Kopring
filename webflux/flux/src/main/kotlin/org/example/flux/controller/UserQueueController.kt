package org.example.flux.controller

import org.example.flux.dto.AllowUserResponse
import org.example.flux.dto.AllowedUserResponse
import org.example.flux.dto.RankNumberResponse
import org.example.flux.dto.RegisterUserResponse
import org.example.flux.service.UserQueueService
import org.springframework.http.ResponseCookie
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/api/v1/queue")
class UserQueueController(
    private val userQueueService: UserQueueService
) {

    @PostMapping("")
    suspend fun registerUser(
        @RequestParam("queue", defaultValue = "default") queue: String,
        @RequestParam("user_id") userId: Long
    ): RegisterUserResponse {
        return RegisterUserResponse(userQueueService.registerWaitQueue(queue, userId))
    }

    @PostMapping("/allow")
    suspend fun allowUser(
        @RequestParam("queue", defaultValue = "default") queue: String,
        @RequestParam("count") count: Long
    ): AllowUserResponse {
        return AllowUserResponse(count, userQueueService.allowUser(queue, count))
    }

    @GetMapping("/allowed")
    suspend fun isAllowedUser(
        @RequestParam("queue", defaultValue = "default") queue: String,
        @RequestParam("user_id") userId: Long,
        @RequestParam("token") token: String
    ): AllowedUserResponse {
        return AllowedUserResponse(userQueueService.isAllowedByToken(queue, userId, token))
    }

    @GetMapping("/rank")
    suspend fun getRankUser(
        @RequestParam("queue", defaultValue = "default") queue: String,
        @RequestParam("user_id") userId: Long
    ): RankNumberResponse {
        return RankNumberResponse(userQueueService.getRank(queue, userId))
    }

    @GetMapping("/touch")
    suspend fun touch(
        @RequestParam("queue", defaultValue = "default") queue: String,
        @RequestParam("user_id") userId: Long,
        exchange: ServerWebExchange
    ): String {
        val token = userQueueService.generateToken(queue, userId)
        exchange.response.addCookie(
            ResponseCookie
                .from(
                    "user-queue-$queue-token",
                    token
                )
                .path("/")
                .maxAge(300)
                .httpOnly(true)
                .build()
        )
        return token
    }
}
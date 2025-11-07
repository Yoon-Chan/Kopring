package org.example.flux.controller

import org.example.flux.dto.AllowUserResponse
import org.example.flux.dto.AllowedUserResponse
import org.example.flux.dto.RegisterUserResponse
import org.example.flux.service.UserQueueService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
    suspend fun isAllowed(@RequestParam("queue", defaultValue = "default") queue: String, @RequestParam("user_id") userId: Long): AllowedUserResponse {
        return AllowedUserResponse(userQueueService.isAllowed(queue, userId))
    }
}
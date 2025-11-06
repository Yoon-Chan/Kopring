package org.example.flux.controller

import org.example.flux.dto.RegisterUserResponse
import org.example.flux.service.UserQueueService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/queue")
class UserQueueController(
    private val userQueueService: UserQueueService
) {

    @PostMapping("")
    fun registerUser(
        @RequestParam("queue", defaultValue = "default") queue: String,
        @RequestParam("user_id") userId: Long
    ): Mono<RegisterUserResponse>  {
        return userQueueService.registerWaitQueue(queue, userId)
            .map(::RegisterUserResponse)
    }
}
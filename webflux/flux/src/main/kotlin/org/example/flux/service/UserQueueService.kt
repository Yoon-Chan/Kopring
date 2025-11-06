package org.example.flux.service

import org.example.flux.exception.ErrorCode
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Instant

@Service
class UserQueueService(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>
) {
    companion object {
        private const val USER_QUEUE_KEY = "users:queue"
    }

    fun registerWaitQueue(queue: String, userId: Long): Mono<Long> {
        //redis sortedset
        // - key : userId
        // - value : unix timestamp
        val unixTimestamp = Instant.now().epochSecond.toDouble()
        return reactiveRedisTemplate.opsForZSet().add("$USER_QUEUE_KEY:$queue:wait", userId.toString(), unixTimestamp)
            .filter { i -> i }
            .switchIfEmpty { Mono.error(ErrorCode.QUEUE_ALREADY_REGISTERED_USER.build()) }
            .flatMap { reactiveRedisTemplate.opsForZSet().rank("$USER_QUEUE_KEY:$queue:wait", userId.toString()) }
            .map { it + 1 }
    }
}
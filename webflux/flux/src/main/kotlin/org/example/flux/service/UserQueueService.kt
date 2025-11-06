package org.example.flux.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.example.flux.exception.ErrorCode
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserQueueService(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>
) {
    companion object {
        private const val USER_QUEUE_KEY = "users:queue"
    }

    suspend fun registerWaitQueue(queue: String, userId: Long): Long {
        //redis sortedset
        // - key : userId
        // - value : unix timestamp
        val unixTimestamp = Instant.now().epochSecond.toDouble()
        val result = reactiveRedisTemplate.opsForZSet().add("$USER_QUEUE_KEY:$queue:wait", userId.toString(), unixTimestamp)
            .awaitSingle()

        //만약 false이면 오류 발생시키기
        if (!result) {
            throw ErrorCode.QUEUE_ALREADY_REGISTERED_USER.build()
        }

        return reactiveRedisTemplate.opsForZSet()
            .rank("$USER_QUEUE_KEY:$queue:wait", userId.toString())
            .awaitSingle() + 1

//        return reactiveRedisTemplate.opsForZSet().add("$USER_QUEUE_KEY:$queue:wait", userId.toString(), unixTimestamp)
//            .asFlow()
//            .filter { i -> i }
//            .onEmpty { throw ErrorCode.QUEUE_ALREADY_REGISTERED_USER.build() }
//            .map { reactiveRedisTemplate.opsForZSet().rank("$USER_QUEUE_KEY:$queue:wait", userId.toString()).awaitSingle() + 1  }

    }
}
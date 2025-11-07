package org.example.flux.service

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.example.flux.exception.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserQueueService(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>
) {
    private val log = LoggerFactory.getLogger(UserQueueService::class.java)
    companion object {
        private const val USER_QUEUE_KEY = "users:queue"
    }

    suspend fun registerWaitQueue(queue: String, userId: Long): Long {
        //redis sortedset
        // - key : userId
        // - value : unix timestamp
        val unixTimestamp = Instant.now().epochSecond.toDouble()
        val result =
            reactiveRedisTemplate.opsForZSet().add("$USER_QUEUE_KEY:$queue:wait", userId.toString(), unixTimestamp)
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

    // 진입이 가능한 상태인지 조회
    // 진입을 허용
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun allowUser(queue: String, count: Long): Long {
        // 1. wait queue에서 사용자를 제거
        // 2. proceed queue 사용자를 추가
        val key = "$USER_QUEUE_KEY:$queue:proceed"
        val waitKey = "$USER_QUEUE_KEY:$queue:wait"
        return reactiveRedisTemplate.opsForZSet().popMin(waitKey, count)
            .asFlow()
            .flatMapConcat { member ->
                reactiveRedisTemplate.opsForZSet().add(key, member.value!!, Instant.now().epochSecond.toDouble())
                    .asFlow()
            }
            .count().toLong()
    }

    //집입이 가능한 상태 조회
    suspend fun isAllowed(queue: String, userId: Long): Boolean {
        val proceedKey = "$USER_QUEUE_KEY:$queue:proceed"
        return reactiveRedisTemplate.opsForZSet().rank(proceedKey, userId.toString())
            .defaultIfEmpty(-1L)
            .map { it >= 0 }
            .awaitSingle()
    }
}
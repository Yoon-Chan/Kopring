package org.example.flux.service

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.example.flux.exception.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.scanAsFlow
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant

@Service
class UserQueueService(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
) {
    private val log = LoggerFactory.getLogger(UserQueueService::class.java)

    @Value("\${scheduler.enabled}")  var scheduling: Boolean = false

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

    suspend fun generateToken(queue: String, userId: Long): String {
        // sha256값 만들기
        val digest = MessageDigest.getInstance("SHA-256")
        val encodeHash = digest.digest("user-queue-$queue-$userId".toByteArray())
        return encodeHash.fold("") { str, it -> str + "%02x".format(it) }
    }

    //집입이 가능한 상태 조회
    suspend fun isAllowed(queue: String, userId: Long): Boolean {
        val proceedKey = "$USER_QUEUE_KEY:$queue:proceed"
        return reactiveRedisTemplate.opsForZSet().rank(proceedKey, userId.toString())
            .defaultIfEmpty(-1L)
            .map { it >= 0 }
            .awaitSingle()
    }

    //집입이 가능한 상태 조회
    suspend fun isAllowedByToken(queue: String, userId: Long, token: String): Boolean {
        if (generateToken(queue, userId) != token) return false
        val proceedKey = "$USER_QUEUE_KEY:$queue:proceed"
        return reactiveRedisTemplate.opsForZSet().rank(proceedKey, userId.toString())
            .defaultIfEmpty(-1L)
            .map { it >= 0 }
            .awaitSingle()
    }

    suspend fun getRank(queue: String, userId: Long): Long {
        val proceedKey = "$USER_QUEUE_KEY:$queue:wait"
        return reactiveRedisTemplate.opsForZSet().rank(proceedKey, userId.toString())
            .defaultIfEmpty(-1L)
            .map { if (it == -1L) -1 else it + 1 }
            .awaitSingle()
    }

    //서버가 시작하고 5초 정도 이후 시작 3초마다 반복
    @Scheduled(initialDelay = 5000, fixedDelay = 3000)
    suspend fun scheduleAllowUser() {
        if(!scheduling) {
            log.info("passed scheduling...")
            return
        }
        log.info("called scheduling...")
        val maxAllowUserCount = 3L
        //사용자 허용
        reactiveRedisTemplate.scanAsFlow(
            ScanOptions
                .scanOptions()
                .match("$USER_QUEUE_KEY:*:wait")
                .count(100)
                .build()
        )
            .map { key -> key.split(":")[2] }
            .map { queue ->
                val allowed = allowUser(queue, maxAllowUserCount)
                Pair(queue, allowed)
            }
            .onEach { (queue, allowed) -> log.info("Tried $maxAllowUserCount and allowed $allowed members of $queue queue") }
            .collect()
    }
}
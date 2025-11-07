package org.example.flux.service

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.example.flux.EmbeddedRedis
import org.example.flux.exception.ApplicationException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@Import(EmbeddedRedis::class)
@ActiveProfiles("test")
class UserQueueServiceTest {
    @Autowired
    lateinit var userQueueService: UserQueueService
    @Autowired
    lateinit var reactiveRedisTemplate: ReactiveRedisTemplate<String, String>

    @BeforeEach
    fun setUp() {
        val redisConnection = reactiveRedisTemplate.connectionFactory.reactiveConnection
        redisConnection.serverCommands().flushAll().subscribe()
    }

    @Test
    fun registerWaitQueue() = runTest {
        assertThat(userQueueService.registerWaitQueue("default", 100L)).isEqualTo(1L)
        assertThat(userQueueService.registerWaitQueue("default", 101L)).isEqualTo(2L)
        assertThat(userQueueService.registerWaitQueue("default", 102L)).isEqualTo(3L)
    }

    @Test
    fun alreadyRegisterQueue() = runTest {
        assertThat(userQueueService.registerWaitQueue("default", 100L)).isEqualTo(1L)
        assertThrows<ApplicationException> {
            userQueueService.registerWaitQueue("default", 100L)
        }
    }

    @Test
    fun allowUser() = runTest {
        assertThat(userQueueService.allowUser("default", 2L)).isEqualTo(0L)
        userQueueService.registerWaitQueue("default", 100L)
        userQueueService.registerWaitQueue("default", 101L)
        userQueueService.registerWaitQueue("default", 102L)
        assertThat(userQueueService.allowUser("default", 2L)).isEqualTo(2L)
    }

    @Test
    fun allowUser2() = runTest {
        userQueueService.registerWaitQueue("default", 100L)
        userQueueService.registerWaitQueue("default", 101L)
        userQueueService.registerWaitQueue("default", 102L)
        assertThat(userQueueService.allowUser("default", 5L)).isEqualTo(3L)
    }

    @Test
    fun allowUserAfterRegisterWaitQueue() = runTest {
        userQueueService.registerWaitQueue("default", 100L)
        userQueueService.registerWaitQueue("default", 101L)
        userQueueService.registerWaitQueue("default", 102L)
        assertThat(userQueueService.allowUser("default", 5L)).isEqualTo(3L)
        assertThat(userQueueService.registerWaitQueue("default", 200L)).isEqualTo(1L)
    }

    @Test
    fun isNotAllowed() = runTest {
        assertThat(userQueueService.isAllowed("default", 100L)).isEqualTo(false)
    }

    @Test
    fun isNotAllowed2() = runTest {
        assertThat(userQueueService.isAllowed("default", 100L)).isEqualTo(false)
        userQueueService.registerWaitQueue("default", 100L)
        userQueueService.allowUser("default", 1L)
        assertThat(userQueueService.isAllowed("default", 101L)).isEqualTo(false)
    }

    @Test
    fun isAllowed() = runTest {
        assertThat(userQueueService.isAllowed("default", 100L)).isEqualTo(false)
        userQueueService.registerWaitQueue("default", 100L)
        userQueueService.allowUser("default", 1L)
        assertThat(userQueueService.isAllowed("default", 100L)).isEqualTo(true)
    }
}
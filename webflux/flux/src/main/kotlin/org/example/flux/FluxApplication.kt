package org.example.flux

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.supervisorScope
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class FluxApplication

suspend fun main(args: Array<String>) {
    runApplication<FluxApplication>(*args)
    SupervisorJob()
    supervisorScope {

    }
}

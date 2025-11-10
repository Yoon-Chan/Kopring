package org.example.flux

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class FluxApplication

fun main(args: Array<String>) {
    runApplication<FluxApplication>(*args)
}

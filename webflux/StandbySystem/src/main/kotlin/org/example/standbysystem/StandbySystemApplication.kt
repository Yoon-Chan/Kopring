package org.example.standbysystem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StandbySystemApplication

fun main(args: Array<String>) {
    runApplication<StandbySystemApplication>(*args)
}

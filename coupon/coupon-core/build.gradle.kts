plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("kapt") version "1.9.25" // 추가
//    kotlin("plugin.jpa")
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "coupon-core"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://company/com/maven2")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.kotest:kotest-assertions-core:5.9.0")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.0")
    testImplementation("io.kotest:kotest-extensions-spring:4.4.3")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    kapt("com.querydsl:querydsl-apt:5.0.0:jpa")
}

kapt {
    includeCompileClasspath = false
    correctErrorTypes = true
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

//// Querydsl 설정부 추가
//val generated = file("src/main/generated")
//
//// querydsl QClass 파일 생성 위치 지정
//tasks.withType<JavaCompile> {
//    options.generatedSourceOutputDirectory.set(generated)
//}

// kotlin source set에 querydsl QClass 위치 추가
sourceSets {
    main {
        java {
            srcDirs("src/main/kotlin", "build/generated/source/kapt/main")
        }
    }
}

// gradle clean 시에 QClass 디렉토리 삭제
//tasks.named("clean") {
//    doLast {
//        generated.deleteRecursively()
//    }
//}

package com.chan.banklecture.types.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Table(name = "user")
@Entity
data class User(
    @Id
    @Column(name = "ulid", length = 26)
    val ulid: String,

    @Column(name = "platform", nullable = false, length = 25)
    val platform: String,

    @Column(name = "username", nullable = false, length = 25)
    val username: String,

    @Column(name = "access_token", nullable = false, length = 50, unique = true)
    val accessToken: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "user")
    val account: List<Account> = mutableListOf()
)
package com.chan.banklecture.common.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

object JsonUtil {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun <T> fromJson(v: String, serializer: KSerializer<T>): T {
        return json.decodeFromString(serializer, v)
    }

    fun <T> encodingToJson(v: T, serializer: KSerializer<T>): String {
        return json.encodeToString(serializer, v)
    }
}
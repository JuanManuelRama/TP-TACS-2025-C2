package com.g7.repo

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import kotlinx.serialization.json.Json

object Cache {
    lateinit var client: StatefulRedisConnection<String, String>

    fun init(redisClient: RedisClient) {
        client = redisClient.connect()
    }

    inline fun <reified T> get(key: String): T? {
        try {
            val value = client.sync().get(key) ?: return null
            return Json.decodeFromString(value)
        } catch (_: Exception) { return null }
    }

    inline fun <reified T> set(key: String, value: T) {
        try {
            val serialized = Json.encodeToString(value)
            client.async().set(key, serialized)
        } catch (_: Exception) { }
    }

    inline fun <reified T> mget(keys: Set<String>): Map<String, T?> {
        if (keys.isEmpty()) return emptyMap()
        try {
            val result = client.sync().mget(*keys.toTypedArray())

            return keys.mapIndexed { index, key ->
                val kv = result.getOrNull(index)
                val value = if (kv == null || kv.isEmpty) null else Json.decodeFromString<T>(kv.value)
                key to value
            }.toMap()
        } catch (_: Exception) { return keys.associateWith { null } }

    }

    // Batch set
    inline fun <reified T> mset(items: Map<String, T>) {
        if (items.isEmpty()) return
        try {
            val commands = client.async()
            items.forEach { (key, value) ->
                val serialized = Json.encodeToString(value)
                commands.set(key, serialized)
            }
        } catch (_: Exception) { }
    }

    fun invalidate(key: String) {
        try {
            client.async().del(key)
        } catch (_: Exception) { }
    }


}
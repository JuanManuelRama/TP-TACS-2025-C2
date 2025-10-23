package com.g7.application.infra

import com.g7.repo.Cache
import io.ktor.server.config.*
import io.lettuce.core.RedisClient

fun configureCache(config: ApplicationConfig) {
    Cache.init(RedisClient.create(config.property("redis.host").getString()))
}
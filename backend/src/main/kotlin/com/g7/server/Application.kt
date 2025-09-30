package com.g7.server

import com.g7.server.middleware.configureMiddleware
import io.ktor.server.application.*
import io.ktor.server.netty.*


fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val mongoUri = environment.config.property("mongo.uri").getString()
    val mongoDb = environment.config.property("mongo.db").getString()

    configureDb(mongoUri, mongoDb)
    configureMiddleware()
    configureRouting()
}


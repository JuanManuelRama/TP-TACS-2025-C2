package com.g7.server

import com.g7.server.middleware.configureMiddleware
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureMiddleware()
    configureRouting()
}


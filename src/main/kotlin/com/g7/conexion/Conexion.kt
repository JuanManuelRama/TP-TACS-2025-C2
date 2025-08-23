package com.g7.conexion

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText("Hello, Ktor!")
        }

        get("/greet/{name}") {
            val name = call.parameters["name"] ?: "world"
            call.respondText("Hello, $name!")
        }
    }
}
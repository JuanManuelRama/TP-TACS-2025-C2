package com.g7.application

import com.g7.routing.eventos.eventos
import com.g7.routing.usuarios.usuarios
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import io.ktor.server.routing.route

fun Application.configureRouting() {
    routing {
        route("/eventos") {
            eventos()
        }
        route("/usuarios") {
            usuarios()
        }
    }
}
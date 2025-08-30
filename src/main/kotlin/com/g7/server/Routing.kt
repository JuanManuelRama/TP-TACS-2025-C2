package com.g7.server

import com.g7.routing.eventos.eventoRoutes
import com.g7.routing.usuarios.usuarioRoutes
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
            log.info("Root endpoint accessed")
        }
        route("/eventos") {
            eventoRoutes()
        }
        route("/usuarios") {
            usuarioRoutes()
        }
    }

}

package com.g7.server

import com.g7.evento.EventoDto
import com.g7.evento.toDomain
import com.g7.evento.toDto
import com.g7.repo.EventoRepository
import com.g7.repo.UsuarioRepository
import com.g7.usuario.UsuarioDto
import com.g7.usuario.toDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*
import java.util.UUID

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
        }
        eventoRoutes()
        post("/usuarios") {
            val usuarioDto = call.receive<UsuarioDto>().copy(id = UUID.randomUUID())
            usuarioDto.toDomain()
                .onSuccess { usuario ->
                    UsuarioRepository.save(usuario)
                    call.respond(HttpStatusCode.Created, usuarioDto)
                }
                .onFailure {
                    call.respond(HttpStatusCode.BadRequest, "Erro al registrar usuario: ${it.message}")
                }
        }
    }
}

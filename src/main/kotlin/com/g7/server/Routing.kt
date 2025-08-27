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
        get("/eventos/{id}") {
            val id = call.requireUuidParam("id")?: return@get
            EventoRepository.findById(id)
                .onSuccess { evento ->
                    call.respond(HttpStatusCode.OK, evento.toDto())
                }
                .onFailure {
                    call.respond(HttpStatusCode.NotFound, "${it.message}")
                }

        }
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
        /**
        * La id de usuario debería entrar por contexto de la request (jwt),
         * por ahora se pasa en el recurso
        * */
        post("/eventos") {
            val eventoDto = call.receive<EventoDto>().copy(id = UUID.randomUUID())
            //TODO: generar una id en serio
            eventoDto.toDomain()
                .onSuccess {
                    EventoRepository.saveEvento(it)
                    call.respond(HttpStatusCode.Created, eventoDto)
                }
                .onFailure {
                    call.respond(HttpStatusCode.BadRequest, "Error al registrar el evento: ${it.message}")
                }
        }

        get("/eventos/{id}/inscriptos") {
            val id = call.requireUuidParam("id")?: return@get

            EventoRepository.findById(id)
                .onSuccess { evento ->
                    call.respond(HttpStatusCode.OK, evento.inscriptos.map { it.usuario.toDto() })
                }
                .onFailure {
                    call.respond(HttpStatusCode.NotFound, "${it.message}")
                }
        }

        //usuarioId debería venir del contexto (jwt)
        put ("eventos/{id}/anotados/{usuarioId}") {
            val id = call.requireUuidParam("id")?: return@put
            EventoRepository.findById(id)
                .onSuccess { evento ->
                    val usuarioId = call.requireUuidParam("usuarioId")?: return@put
                    UsuarioRepository.getUsuarioFromId(usuarioId)
                        .onSuccess { usuario ->
                            evento.inscribir(usuario)
                                .onSuccess { call.respond(HttpStatusCode.OK) }
                                .onFailure { call.respond(HttpStatusCode.BadRequest, "${it.message}") }
                        }
                        .onFailure {
                            call.respond(HttpStatusCode.NotFound, "${it.message}")
                        } }
                .onFailure { call.respond(HttpStatusCode.NotFound, "Evento no encontrado") }
        }
    }
}

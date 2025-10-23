package com.g7.routing.eventos.id.inscriptos

import com.g7.evento.toDto
import com.g7.repo.EventoRepo
import com.g7.repo.UsuarioRepo
import com.g7.routing.eventos.id.inscriptos.userId.eventosIdInscripcionUserId
import com.g7.application.middleware.login.loggedUser
import com.g7.application.requireIdParam
import com.g7.service.EventoService
import com.g7.service.UsuarioService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.eventosIdInscriptos() {
    authenticate("auth-jwt") {
        get {
            val id = call.requireIdParam("id")
            val user = call.loggedUser()
            val owner = EventoService.getEventOwner(id)

            if (user.id != owner) {
                throw IllegalAccessException("Solo el organizador puede ver los inscriptos")
            }

            val inscriptos = EventoRepo.batchGetInscripto(id)
            val usuarios = inscriptos.inscriptos + inscriptos.esperas

            if (usuarios.isEmpty()) {
                call.respond(HttpStatusCode.OK, emptyList<Any>())
                return@get
            }

            val usuariosMap = UsuarioService.getUsuarios(usuarios.toSet())
            call.respond(HttpStatusCode.OK, inscriptos.toDto(usuariosMap))

        }
        post {
            val eventoId = call.requireIdParam("id")
            val userId = call.loggedUser().id
            val owner = EventoService.getEventOwner(eventoId)

            if (userId == owner) {
                throw IllegalStateException("El organizador no puede inscribirse a su propio evento")
            }

            val inscripcion = EventoRepo.inscribirUsuario(eventoId, userId)
            UsuarioRepo.inscribirEvento(userId, eventoId, inscripcion.confirmado)
            call.respond(HttpStatusCode.Created, inscripcion.toDto(UsuarioRepo))
        }
        delete{
            val eventoId = call.requireIdParam("id")
            val userId = call.loggedUser().id

            EventoRepo.cancelarInscripcion(eventoId, userId)
            UsuarioRepo.descinscribirEvento(userId, eventoId)
            call.respond(HttpStatusCode.NoContent)
        }
    }
    route("/{userId}") {
        eventosIdInscripcionUserId()
    }
}
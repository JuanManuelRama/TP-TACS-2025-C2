package com.g7.routing.eventos.id.inscriptos.userId

import com.g7.evento.toDto
import com.g7.repo.EventoRepo
import com.g7.repo.UsuarioRepo
import com.g7.application.middleware.login.loggedUser
import com.g7.application.requireIdParam
import com.g7.usuario.toResponseDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get

fun Route.eventosIdInscripcionUserId() {
    authenticate("auth-jwt") {
        get {
            val eventoId = call.requireIdParam("id")
            val userId = call.requireIdParam("userId")
            val loggedUser = call.loggedUser()

            if(loggedUser.id == userId) {
                val inscripcion = EventoRepo.getInscripcion(eventoId, userId)
                call.respond(HttpStatusCode.OK, inscripcion.toDto(loggedUser.toResponseDto()))
            }

            val owner = EventoRepo.getOwnerFromId(eventoId)

            if (loggedUser.id != owner)  {
                throw IllegalAccessException("No podes ver la inscripcion de otro usuario")
            }
            val inscripcion = EventoRepo.getInscripcion(eventoId, userId)
            val inscripto = UsuarioRepo.getFromId(userId).toResponseDto()
            call.respond(HttpStatusCode.OK, inscripcion.toDto(inscripto))
        }

        delete {
            val eventoId = call.requireIdParam("id")
            val loggedUser = call.loggedUser()

            val owner = EventoRepo.getOwnerFromId(eventoId)

            if (loggedUser.id != owner) {
                throw IllegalAccessException("Solo el organizador puede dar de baja a un inscripto")
            }

            val userId = call.requireIdParam("userId")
            EventoRepo.cancelarInscripcion(eventoId, userId)
            UsuarioRepo.descinscribirEvento(userId, eventoId)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
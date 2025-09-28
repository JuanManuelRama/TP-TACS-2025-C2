package com.g7.routing.eventos

import com.g7.evento.EventoInputDto
import com.g7.evento.toDto
import com.g7.server.*
import com.g7.server.middleware.login.loggedUser
import com.g7.usuario.dto.toResponseDto
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.eventoLoggedRoutes() {
    post {
        val user = call.loggedUser()
        val eventoDto = call.receive<EventoInputDto>()
        val evento = application.eventoRepo.save(user.id, eventoDto)
        application.usuarioRepo.crearEvento(user.id, evento.id)
        call.respond(HttpStatusCode.Created, evento.toDto(user.toResponseDto()))
    }

    delete("/{id}") {
        val id = call.requireIdParam("id")
        val user = call.loggedUser()
        val organizador = application.eventoRepo.getOwnerFromId(id)

        if (user.id != organizador) {
            throw IllegalAccessException("Solo el organizador puede eliminar el evento")
        }
        application.usuarioRepo.borrarEventoCreado(user.id, id)
        val inscriptos = application.eventoRepo.batchGetInscripcion(id).toSet()
        val ids = inscriptos.map { it.usuario }.toSet()
        application.usuarioRepo.batchDescinscribirEvento(ids, id)
        application.eventoRepo.deleteEvento(id)
        call.respond(HttpStatusCode.NoContent)
    }

    get("/{id}/inscriptos") {
        val id = call.requireIdParam("id")
        val user = call.loggedUser()
        val owner = application.eventoRepo.getOwnerFromId(id)

        if (user.id != owner) {
            throw IllegalAccessException("Solo el organizador puede ver los inscriptos")
        }

        val inscriptos = application.eventoRepo.batchGetInscripcion(id)
        val usuariosMap = application.usuarioRepo.batchGetFromId(inscriptos.map { it.usuario }
            .toSet()).mapValues {it.value.toResponseDto()}
        call.respond(HttpStatusCode.OK, inscriptos.map { it.toDto(usuariosMap[it.usuario]!!) })

    }

    post("/{id}/inscriptos") {
        val eventoId = call.requireIdParam("id")
        val userId = call.loggedUser().id
        val owner = application.eventoRepo.getOwnerFromId(eventoId)

        if (userId == owner) {
            throw IllegalStateException("El organizador no puede inscribirse a su propio evento")
        }


        val inscripcion = application.eventoRepo.inscribirUsuario(eventoId, userId)
        application.usuarioRepo.inscribirEvento(userId, eventoId, inscripcion.confirmado)
        call.respond(HttpStatusCode.Created, inscripcion.toDto(application.usuarioRepo))
    }

    delete("/{id}/inscriptos") {
        val eventoId = call.requireIdParam("id")
        val userId = call.loggedUser().id

        application.eventoRepo.cancelarInscripcion(eventoId, userId)
        call.respond(HttpStatusCode.NoContent)
    }

    get("/{id}/inscriptos/{userId}") {
        val eventoId = call.requireIdParam("id")
        val userId = call.requireIdParam("userId")
        val loggedUser = call.loggedUser()

        if(loggedUser.id == userId) {
            val inscripcion = application.eventoRepo.getInscripcion(eventoId, userId)
            call.respond(HttpStatusCode.OK, inscripcion.toDto(loggedUser.toResponseDto()))
        }

        val owner = application.eventoRepo.getOwnerFromId(eventoId)

        if (loggedUser.id != owner)  {
           throw IllegalAccessException("No podes ver la inscripcion de otro usuario")
        }
        val inscripcion = application.eventoRepo.getInscripcion(eventoId, userId)
        val inscripto = application.usuarioRepo.getFromId(userId).toResponseDto()
        call.respond(HttpStatusCode.OK, inscripcion.toDto(inscripto))
    }

    delete("/{id}/inscriptos/{userId}") {
        val id = call.requireIdParam("id")
        val loggedUser = call.loggedUser()

        val owner = application.eventoRepo.getOwnerFromId(id)

        if (loggedUser.id != owner) {
            throw IllegalAccessException("Solo el organizador puede dar de baja a un inscripto")
        }

        val userId = call.requireIdParam("userId")
        application.eventoRepo.cancelarInscripcion(id, userId)
        call.respond(HttpStatusCode.NoContent)
    }
}
package com.g7.routing.eventos

import com.g7.evento.EventoInputDto
import com.g7.evento.toDto
import com.g7.repo.EventoRepo
import com.g7.repo.UsuarioRepo
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
        val evento = EventoRepo.save(user.id, eventoDto)
        UsuarioRepo.crearEvento(user.id, evento.id)
        call.respond(HttpStatusCode.Created, evento.toDto(user.toResponseDto()))
    }

    delete("/{id}") {
        val id = call.requireIdParam("id")
        val user = call.loggedUser()
        val organizador = EventoRepo.getOwnerFromId(id)

        if (user.id != organizador) {
            throw IllegalAccessException("Solo el organizador puede eliminar el evento")
        }
        UsuarioRepo.borrarEventoCreado(user.id, id)
        val inscriptos = EventoRepo.batchGetInscripcion(id).toSet()
        val ids = inscriptos.map { it.usuario }.toSet()
        UsuarioRepo.batchDescinscribirEvento(ids, id)
        EventoRepo.deleteEvento(id)
        call.respond(HttpStatusCode.NoContent)
    }

    get("/{id}/inscriptos") {
        val id = call.requireIdParam("id")
        val user = call.loggedUser()
        val owner = EventoRepo.getOwnerFromId(id)

        if (user.id != owner) {
            throw IllegalAccessException("Solo el organizador puede ver los inscriptos")
        }

        val inscriptos = EventoRepo.batchGetInscripcion(id)
        val usuariosMap = UsuarioRepo.batchGetFromId(inscriptos.map { it.usuario }
            .toSet()).mapValues {it.value.toResponseDto()}
        call.respond(HttpStatusCode.OK, inscriptos.map { it.toDto(usuariosMap[it.usuario]!!) })

    }

    post("/{id}/inscriptos") {
        val eventoId = call.requireIdParam("id")
        val userId = call.loggedUser().id
        val owner = EventoRepo.getOwnerFromId(eventoId)

        if (userId == owner) {
            throw IllegalStateException("El organizador no puede inscribirse a su propio evento")
        }

        val inscripcion = EventoRepo.inscribirUsuario(eventoId, userId)
        UsuarioRepo.inscribirEvento(userId, eventoId, inscripcion.confirmado)
        call.respond(HttpStatusCode.Created, inscripcion.toDto(UsuarioRepo))
    }

    delete("/{id}/inscriptos") {
        val eventoId = call.requireIdParam("id")
        val userId = call.loggedUser().id

        EventoRepo.cancelarInscripcion(eventoId, userId)
        UsuarioRepo.descinscribirEvento(userId, eventoId)
        call.respond(HttpStatusCode.NoContent)
    }

    get("/{id}/inscriptos/{userId}") {
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

    delete("/{id}/inscriptos/{userId}") {
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
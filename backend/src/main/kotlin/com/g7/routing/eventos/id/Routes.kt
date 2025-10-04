package com.g7.routing.eventos.id

import com.g7.evento.toDto
import com.g7.repo.EventoRepo
import com.g7.repo.UsuarioRepo
import com.g7.routing.eventos.id.inscriptos.eventosIdInscriptos
import com.g7.application.middleware.login.loggedUser
import com.g7.application.requireIdParam
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.eventosId() {
    get {
        val id = call.requireIdParam("id")
        val evento = EventoRepo.getFromId(id)
        call.respond(HttpStatusCode.OK, evento.toDto(UsuarioRepo))
    }
    authenticate("auth-jwt") {
        delete {
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
    }
    route("/inscriptos") {
        eventosIdInscriptos()
    }
}
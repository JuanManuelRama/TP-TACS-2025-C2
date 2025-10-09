package com.g7.routing.eventos.id.estadisticas

import com.g7.application.requireIdParam
import com.g7.evento.EstadisticasEvento
import com.g7.repo.EventoRepo
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.eventosIdEstadisticas() {
    authenticate("auth-jwt") {
        get {
            val id = call.requireIdParam("id")
            val evento = EventoRepo.getFromId(id)
            call.respond(HttpStatusCode.OK, EstadisticasEvento(evento))
        }
    }

}
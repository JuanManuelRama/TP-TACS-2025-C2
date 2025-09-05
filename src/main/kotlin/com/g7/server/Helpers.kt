package com.g7.server

import com.g7.evento.Evento
import com.g7.repo.EventoRepository
import com.g7.repo.UsuarioRepository
import com.g7.usuario.Usuario
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import java.util.UUID

/**
* Busca el parámetro especificado de una UUID, si no lo encuentra envía la respuesta correspondiente
* y retorna null
* */
suspend fun ApplicationCall.requireUuidParam(name: String): UUID? {
    val raw = parameters[name] ?: run {
        respond(HttpStatusCode.BadRequest, "Missing $name")
        return null
    }
    return try {
        UUID.fromString(raw)
    } catch (_: IllegalArgumentException) {
        respond(HttpStatusCode.BadRequest, "Invalid $name format")
        null
    }
}

/**
 * Busca el evento correspondiente al ID, retorna null si no lo encontró, y en ese caso responde por http
 * esta función existe para manejar de forma centralizada todos los casos distintos de error
 * aunque por ahora solo haya uno (no encontrado)
 * */
suspend fun ApplicationCall.fetchEvento(id: UUID): Evento? {
    val result = EventoRepository.findById(id)
    return result.getOrElse {
        respondError(HttpStatusCode.NotFound, it.message)
        null
    }
}

/**
 * Busca el evento correspondiente al ID, retorna null si no lo encontró, y en ese caso responde por http
 * esta función existe para manejar de forma centralizada todos los casos distintos de error
 * aunque por ahora solo haya uno (no encontrado)
 * */
suspend fun ApplicationCall.fetchUsuario(id: UUID): Usuario? {
    val result = UsuarioRepository.getUsuarioFromId(id)
    return result.getOrElse {
        respondError(HttpStatusCode.NotFound, it.message)
        null
    }
}

/**
 * Garantiza el formato de todos los errores, además de manejar centralizado el caso de que no haya
 * mensaje. En un futuro también se podría utilizar para los logs
 * */
suspend fun ApplicationCall.respondError(status: HttpStatusCode, message: String? = null) {
    respond(status, mapOf("error" to (message ?: "Unknown error")))
}
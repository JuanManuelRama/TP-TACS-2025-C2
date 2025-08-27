package com.g7.server

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import java.util.UUID

/**
* Busca el parámetro especificado de una UUID, si no lo encuentra envía la respuesta correspondiente
* y retorna [null]
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
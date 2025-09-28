package com.g7.server

import com.g7.exception.InvalidIdException
import com.g7.exception.MissingParameterException
import io.ktor.server.application.*
import org.bson.types.ObjectId

/**
 * Busca el parámetro especificado de una ObjectId en la llamada.
 * @throws MissingParameterException si no se encuentra el parámetro
 * @throws InvalidIdException si el parámetro no es un ObjectId válido
* */
fun ApplicationCall.requireIdParam(name: String): ObjectId {
    val raw = parameters[name] ?: run {
        throw MissingParameterException(name)
}
    return try {
        ObjectId(raw)
    } catch (_: IllegalArgumentException) {
        throw InvalidIdException(raw)
    }
}
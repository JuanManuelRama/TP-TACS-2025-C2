package com.g7.server

import com.g7.repo.EventoRepo
import com.g7.repo.MongoProvider
import com.g7.repo.UsuarioRepo
import io.ktor.server.application.Application
import io.ktor.util.AttributeKey

fun Application.configureDb(mongoUri: String, mongoDb: String) {
    val mongoProvider = MongoProvider(mongoUri, mongoDb)
    mongoProvider.init()

    attributes.put(AttributeKey("usuarioRepo"), UsuarioRepo(mongoProvider))
    attributes.put(AttributeKey("eventoRepo"), EventoRepo(mongoProvider))
}
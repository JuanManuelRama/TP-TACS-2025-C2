package com.g7.application.infra

import com.g7.repo.EventoRepo
import com.g7.repo.MongoProvider
import com.g7.repo.UsuarioRepo
import io.ktor.server.config.ApplicationConfig

fun configureDb(config: ApplicationConfig) {
    MongoProvider.init(config.property("mongo.uri").getString(), config.property("mongo.db").getString())
    UsuarioRepo.init()
    EventoRepo.init()
}
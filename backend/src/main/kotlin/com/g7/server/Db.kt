package com.g7.server

import com.g7.repo.EventoRepo
import com.g7.repo.MongoProvider
import com.g7.repo.UsuarioRepo

fun configureDb(mongoUri: String, mongoDb: String) {
    MongoProvider.init(mongoUri, mongoDb)
    UsuarioRepo.init()
    EventoRepo.init()
}
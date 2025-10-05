package com.g7.application

import com.g7.application.infra.configureDb
import com.g7.application.middleware.*
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain


fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    //Infra
    configureDb(environment.config)

    //Middleware
    installAuth(environment.config)
    installStatusPages()
    installContentNegotiation()
    installLogging()
    //installCors()

    //Routing
    configureRouting()
}


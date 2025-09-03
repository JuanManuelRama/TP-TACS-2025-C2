package com.g7.server

import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    JwtConfig.init(environment.config)

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(JwtConfig.verifier)
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()
                if (userId != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    configureRouting()
}


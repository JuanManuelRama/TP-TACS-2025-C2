package com.g7.application.middleware

import com.g7.application.middleware.login.JwtConfig
import com.g7.exception.InvalidCredentialsException
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.config.ApplicationConfig

fun Application.installAuth(config: ApplicationConfig) {
    JwtConfig.init(config)

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(JwtConfig.verifier)
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()
                if (userId != null) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                throw InvalidCredentialsException("Token inválido o expirado")
            }
        }
    }
}
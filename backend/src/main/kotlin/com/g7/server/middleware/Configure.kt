package com.g7.server.middleware

import com.g7.exception.InvalidCredentialsException
import com.g7.server.middleware.login.JwtConfig
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level


fun Application.configureMiddleware() {
    JwtConfig.init(environment.config)

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

    configureExceptions()

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            }
        )
    }

    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Delete)


        allowMethod(HttpMethod.Options) // important for preflight

        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)

        allowCredentials = true

        // For dev
        allowHost("localhost:5173", schemes = listOf("http"))
        //For prod
        allowHost("localhost:8081", schemes = listOf("http"))

        // In prod, you can add your real frontend domain:
        // allowHost("app.mydomain.com", schemes = listOf("https"))
    }

    install(CallLogging) {
        level = Level.INFO
        filter { it.request.path().startsWith("/") }
    }


}
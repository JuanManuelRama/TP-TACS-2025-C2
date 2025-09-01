package com.g7.server

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.g7.usuario.Usuario
import io.ktor.server.config.ApplicationConfig
import java.util.Date

object JwtConfig {
    private lateinit var secret: String
    private lateinit var issuer: String
    private lateinit var audience: String
    private const val validityInMs = 36_000_00 * 10 // 10 horas

    private lateinit var algorithm: Algorithm
    lateinit var verifier: JWTVerifier private set

    fun init(config: ApplicationConfig) {
        secret = config.property("jwt.secret").getString()
        issuer = config.property("jwt.issuer").getString()
        audience = config.property("jwt.audience").getString()

        algorithm = Algorithm.HMAC512(secret)
        verifier = JWT
            .require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .build()
    }

    fun generateToken(usuario: Usuario): String =
        JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username", usuario.username)
            .withClaim("type", usuario.type.name)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(algorithm)
}
package com.g7.server

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.g7.usuario.Usuario
import io.ktor.server.config.ApplicationConfig
import java.util.Date
import kotlin.properties.Delegates

object JwtConfig {
    private lateinit var secret: String
    private lateinit var issuer: String
    private lateinit var audience: String
    private var validityInMs by Delegates.notNull<Long>()

    lateinit var userIdClaimName: String
    lateinit var usernameClaimName: String
    lateinit var typeClaimName: String

    private lateinit var algorithm: Algorithm
    lateinit var verifier: JWTVerifier private set

    fun init(config: ApplicationConfig) {
        secret = config.property("jwt.secret").getString()
        issuer = config.property("jwt.issuer").getString()
        audience = config.property("jwt.audience").getString()
        validityInMs = config.property("jwt.validity").getString().toLong()

        userIdClaimName = config.property("jwt.claims.userId").getString()
        usernameClaimName = config.property("jwt.claims.username").getString()
        typeClaimName = config.property("jwt.claims.type").getString()

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
            .withClaim(userIdClaimName, usuario.id.toString())
            .withClaim(usernameClaimName, usuario.username)
            .withClaim(typeClaimName, usuario.type.name)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(algorithm)
}
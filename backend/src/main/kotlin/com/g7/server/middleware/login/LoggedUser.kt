package com.g7.server.middleware.login

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.plugins.BadRequestException
import org.bson.types.ObjectId
import java.util.UUID
import javax.naming.AuthenticationException

data class LoggedUser(
    val id: ObjectId,
    val username: String,
    val type: String
)

fun ApplicationCall.loggedUser(): LoggedUser {
    val principal = principal<JWTPrincipal>()
        ?: throw AuthenticationException("Missing or invalid JWT") // nunca debería pasar, middleware se encarga

    val idClaim = principal.payload.getClaim(JwtConfig.userIdClaimName).asString()
        ?: throw BadRequestException("Missing user ID claim")
    val username = principal.payload.getClaim(JwtConfig.usernameClaimName).asString()
        ?: throw BadRequestException("Missing username claim")
    val type = principal.payload.getClaim(JwtConfig.typeClaimName).asString()
        ?: throw BadRequestException("Missing user type claim")

    val id = try {
        ObjectId(idClaim)
    } catch (_: IllegalArgumentException) {
        throw BadRequestException("Invalid user ID format")
    }

    return LoggedUser(id, username, type)
}
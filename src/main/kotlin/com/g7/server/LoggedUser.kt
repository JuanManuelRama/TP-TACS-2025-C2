package com.g7.server

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import java.util.UUID

data class LoggedUser(
    val id: UUID,
    val username: String,
    val type: String
)

fun ApplicationCall.loggedUser(): LoggedUser? {
    val principal = this.principal<JWTPrincipal>() ?: return null

    val id = UUID.fromString(principal.payload.getClaim(JwtConfig.userIdClaimName).asString())
    val username = principal.payload.getClaim(JwtConfig.usernameClaimName).asString()
    val type = principal.payload.getClaim(JwtConfig.typeClaimName).asString()

    return if (id != null && username != null && type != null) {
        LoggedUser(id, username, type)
    } else null
}
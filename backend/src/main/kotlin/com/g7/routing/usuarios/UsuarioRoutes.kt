package com.g7.routing.usuarios;

import com.g7.repo.UsuarioRepository
import com.g7.server.middleware.login.JwtConfig
import com.g7.server.requireUuidParam
import com.g7.server.respondError
import com.g7.usuario.dto.LoginRequestDto
import com.g7.usuario.dto.UsuarioDto
import com.g7.usuario.dto.toResponseDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.usuarioRoutes() {

    post {
        val usuarioDto = call.receive<UsuarioDto>()

        val usuario = UsuarioRepository.getOptionalUsuarioFromUsername(usuarioDto.username)

        if (usuario != null) {
            call.respondError(HttpStatusCode.Conflict, "El nombre de usuario ya existe")
            return@post
        }

        usuarioDto.register()
            .onSuccess {
                usuario ->
                    UsuarioRepository.save(usuario)
                    call.respond(HttpStatusCode.Created, usuarioDto.toResponseDto())
            }
            .onFailure {
                call.respondError(HttpStatusCode.BadRequest, "Error al inscribir usuario: ${it.message}")
            }
    }

    get("/{id}"){
        val id = call.requireUuidParam("id") ?: return@get

        UsuarioRepository.getUsuarioFromId(id)
            .onSuccess { usuario ->
                call.respond(HttpStatusCode.OK, usuario.toResponseDto())
            }
            .onFailure {
                call.respondError(HttpStatusCode.NotFound, "${it.message}")
            }
    }

    post("/login") {
        val loginRequest = call.receive<LoginRequestDto>()
        val usuario = UsuarioRepository.getOptionalUsuarioFromUsername(loginRequest.username)

        if (usuario == null || !usuario.passwordMatches(loginRequest.password)) {
            call.respondError(HttpStatusCode.Unauthorized, "Usuario o contraseña incorrectos")
            return@post
        }

        call.respond(mapOf("token" to JwtConfig.generateToken(usuario)))
    }

}

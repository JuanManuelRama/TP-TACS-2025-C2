package com.g7.routing.usuarios;

import com.g7.exception.InvalidCredentialsException
import com.g7.server.middleware.login.JwtConfig
import com.g7.server.requireIdParam
import com.g7.server.usuarioRepo
import com.g7.usuario.dto.LoginRequestDto
import com.g7.usuario.dto.LoginResponseDto
import com.g7.usuario.dto.UsuarioInputDto
import com.g7.usuario.dto.toResponseDto
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usuarioRoutes() {

    post {
        val usuarioDto = call.receive<UsuarioInputDto>()

        val newUsuario = application.usuarioRepo.save(usuarioDto)
        call.respond(HttpStatusCode.Created, newUsuario.toResponseDto())
    }

    get("/{id}"){
        val id = call.requireIdParam("id")

        call.respond(HttpStatusCode.OK, application.usuarioRepo.getFromId(id).toResponseDto())
    }

    post("/login") {
        val loginRequest = call.receive<LoginRequestDto>()
        val usuario = application.usuarioRepo.getFromUsername(loginRequest.username)

        if (!usuario.passwordMatches(loginRequest.password)) {
            throw InvalidCredentialsException()
        }

        call.respond(HttpStatusCode.OK, LoginResponseDto(
            token = JwtConfig.generateToken(usuario),
            user = usuario.toResponseDto()
        ))
    }

}

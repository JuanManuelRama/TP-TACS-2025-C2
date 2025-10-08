package com.g7.routing.usuarios.login

import com.g7.exception.InvalidCredentialsException
import com.g7.repo.UsuarioRepo
import com.g7.application.middleware.login.JwtConfig
import com.g7.usuario.LoginRequestDto
import com.g7.usuario.LoginResponseDto
import com.g7.usuario.toResponseDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.usuariosLogin() {
    post {
        val loginRequest = call.receive<LoginRequestDto>()
        val usuario = UsuarioRepo.getFromUsername(loginRequest.username)

        if (!usuario.passwordMatches(loginRequest.password)) {
            throw InvalidCredentialsException()
        }

        call.respond(HttpStatusCode.OK, LoginResponseDto(
            token = JwtConfig.generateToken(usuario),
            user = usuario.toResponseDto()
        ))
    }
}
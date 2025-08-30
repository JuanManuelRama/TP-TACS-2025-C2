package com.g7.routing.usuarios;

import com.g7.repo.UsuarioRepository
import com.g7.usuario.UsuarioDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import java.util.UUID

fun Route.usuarioRoutes() {

    post {

        val usuarioDto = call.receive < UsuarioDto > ().copy(id = UUID.randomUUID())

        usuarioDto.toDomain()
            .onSuccess {
                usuario ->
                    UsuarioRepository.save(usuario)
                    call.respond(HttpStatusCode.Created, usuarioDto)
            }
            .onFailure {
                call.respond(HttpStatusCode.BadRequest, "Erro al inscribir usuario: ${it.message}")
            }

    }

}

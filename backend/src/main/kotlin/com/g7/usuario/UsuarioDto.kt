package com.g7.usuario

import com.g7.application.middleware.login.LoggedUser
import com.g7.evento.EventoResponseDto
import com.g7.exception.InvalidConstructorException
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class UsuarioResponseDto(
    val id: String,
    val username: String,
    val type: UserType? = null
)

fun Usuario.toResponseDto(): UsuarioResponseDto = UsuarioResponseDto(id, username, type)

fun LoggedUser.toResponseDto(): UsuarioResponseDto =
    UsuarioResponseDto(id, username, UserType.from(type))

@Serializable
data class UsuarioInputDto(
    val username: String,
    val password: String,
    val type: UserType,
) {
    fun hashPassword(): String = BCrypt.hashpw(password, BCrypt.gensalt())

    fun validate() {
        if (username.length !in 3..40) {
            throw InvalidConstructorException("El nombre de usuario debe tener entre 3 y 30 caracteres")
        }
        if (password.length < 6) {
            throw InvalidConstructorException("La contraseña debe tener al menos 6 caracteres")
        }
    }
}


@Serializable
data class LoginRequestDto (
    val username: String = "",
    val password: String = ""
)

@Serializable
data class LoginResponseDto (
    val token: String,
    val user: UsuarioResponseDto
)

@Serializable
data class UsuarioEventosDto (
    val eventosConfirmados: List<EventoResponseDto>,
    val eventosEnEspera: List<EventoResponseDto>,
    val eventosCreados: List<EventoResponseDto>
)

fun UsuarioEventos.toDto(map: Map<String, EventoResponseDto>): UsuarioEventosDto = UsuarioEventosDto(
    eventosConfirmados = eventosConfirmados?.map { map[it]!! } ?: emptyList(),
    eventosEnEspera = eventosEnEspera?.map { map[it]!! } ?: emptyList(),
    eventosCreados = eventosCreados?.map { map[it]!! } ?: emptyList()
)
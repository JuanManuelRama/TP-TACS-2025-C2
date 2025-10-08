package com.g7.usuario

import com.g7.serializable.ObjectIdSerializer
import com.g7.application.middleware.login.LoggedUser
import com.g7.evento.EventoResponseDto
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class UsuarioResponseDto(
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId,
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

fun UsuarioEventos.toDto(map: Map<ObjectId, EventoResponseDto>): UsuarioEventosDto = UsuarioEventosDto(
    eventosConfirmados = eventosConfirmados?.map { map[it]!! } ?: emptyList(),
    eventosEnEspera = eventosEnEspera?.map { map[it]!! } ?: emptyList(),
    eventosCreados = eventosCreados?.map { map[it]!! } ?: emptyList()
)
package com.g7.usuario.dto

import com.g7.serializable.UUIDSerializer
import com.g7.usuario.UserType
import com.g7.usuario.Usuario
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UsuarioResponseDto(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,
    val username: String,
    val password: String? = null,
    val type: UserType? = null
)

fun UsuarioDto.toResponseDto(): UsuarioResponseDto = UsuarioResponseDto(id, username, null, type)

fun Usuario.toResponseDto(): UsuarioResponseDto = UsuarioResponseDto(id, username, password, type) // no exponer el password, lo dejo para pruebas

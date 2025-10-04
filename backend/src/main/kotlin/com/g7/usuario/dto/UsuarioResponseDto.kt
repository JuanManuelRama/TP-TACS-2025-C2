package com.g7.usuario.dto

import com.g7.serializable.ObjectIdSerializer
import com.g7.application.middleware.login.LoggedUser
import com.g7.usuario.UserType
import com.g7.usuario.Usuario
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

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

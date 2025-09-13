package com.g7.usuario.dto

import kotlinx.serialization.Serializable

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
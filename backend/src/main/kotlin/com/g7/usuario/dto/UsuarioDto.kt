package com.g7.usuario.dto

import com.g7.usuario.UserType
import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class UsuarioInputDto(
    val username: String,
    val password: String,
    val type: UserType,
) {
    fun hashPassword(): String = BCrypt.hashpw(password, BCrypt.gensalt())
}

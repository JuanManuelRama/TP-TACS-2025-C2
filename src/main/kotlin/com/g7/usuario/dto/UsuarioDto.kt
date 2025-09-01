package com.g7.usuario.dto

import com.g7.serializable.UUIDSerializer
import com.g7.usuario.UserType
import com.g7.usuario.Usuario
import kotlinx.serialization.Serializable
import java.util.UUID
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class UsuarioDto(
    @Serializable(with = UUIDSerializer::class)
    var id: UUID? = null,
    val username: String,
    val password: String,
    val type: UserType,
) {
    fun hashPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())

    fun register(): Result<Usuario> {
        id = UUID.randomUUID()
        return Result.success(Usuario(id!!, username, hashPassword(password), type))
    }
}

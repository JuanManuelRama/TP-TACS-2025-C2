package com.g7.usuario

import com.g7.serializable.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UsuarioDto(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,
    val nombre: String
) {
    fun toDomain(): Result<Usuario>{
        if (id == null) {
            return Result.failure(RuntimeException("id es null"))
        }
        return Result.success(Usuario(id, nombre))
    }
}

fun Usuario.toDto(): UsuarioDto = UsuarioDto(id, nombre)
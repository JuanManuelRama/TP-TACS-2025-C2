package com.g7.usuario

import kotlinx.serialization.Serializable

@Serializable
class UsuarioDto(val nombre: String) {
    fun toDomain(): Usuario = Usuario(nombre)
}

fun Usuario.toDto(): UsuarioDto = UsuarioDto(nombre)
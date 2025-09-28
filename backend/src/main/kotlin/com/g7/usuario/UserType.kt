package com.g7.usuario

enum class UserType {

    ORGANIZADOR, PARTICIPANTE;

    companion object {
        fun from(type: String): UserType {
            return when (type.uppercase()) {
                "ORGANIZADOR" -> ORGANIZADOR
                "PARTICIPANTE" -> PARTICIPANTE
                else -> throw IllegalArgumentException("Tipo de usuario inválido: $type")
            }
        }
    }
}
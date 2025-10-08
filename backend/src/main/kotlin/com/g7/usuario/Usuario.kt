package com.g7.usuario

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.mindrot.jbcrypt.BCrypt

data class Usuario (
    @param:BsonId val id: ObjectId = ObjectId(),
    val username: String,
    val password: String,
    val type: UserType,
) {

    fun passwordMatches(password: String): Boolean {
        return BCrypt.checkpw(password, this.password)
    }
}

data class UsuarioEventos(
    val eventosConfirmados: List<ObjectId>? = emptyList(),
    val eventosEnEspera: List<ObjectId>? = emptyList(),
    val eventosCreados: List<ObjectId>? = emptyList()
)
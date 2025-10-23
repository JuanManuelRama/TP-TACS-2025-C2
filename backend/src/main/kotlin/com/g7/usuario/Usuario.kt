package com.g7.usuario

import org.bson.BsonType
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonRepresentation
import org.bson.types.ObjectId
import org.mindrot.jbcrypt.BCrypt

data class Usuario (
    @param:BsonId
    @field:BsonRepresentation(BsonType.OBJECT_ID)
    val id: String = ObjectId().toHexString(),
    val username: String,
    val password: String,
    val type: UserType,
) {

    fun passwordMatches(password: String): Boolean {
        return BCrypt.checkpw(password, this.password)
    }
}

data class UsuarioEventos(
    val eventosConfirmados: List<String>? = emptyList(),
    val eventosEnEspera: List<String>? = emptyList(),
    val eventosCreados: List<String>? = emptyList()
)

val UnknownUser: Usuario = Usuario(
    id = "000000000000000000000000",
    username = "unknown",
    password = "",
    type = UserType.PARTICIPANTE,
)
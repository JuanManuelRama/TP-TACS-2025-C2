package com.g7.repo

import com.g7.exception.InvalidCredentialsException
import com.g7.usuario.Usuario
import com.g7.usuario.UsuarioEventos
import com.g7.usuario.UsuarioInputDto
import com.mongodb.MongoWriteException
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Updates

object UsuarioRepo {
    private lateinit var collection: MongoCollection<Usuario>
    private val projection = Projections.exclude("eventosCreados", "eventosConfirmados", "EventosEnEspera")

    fun init() {
        collection = MongoProvider.usuarioCollection
    }

    fun getUsuarios(): List<Usuario> {
        return collection.find()
            .projection(projection)
            .into(ArrayList())
    }

    fun save(usuario: UsuarioInputDto): Usuario {
        val newUsuario = Usuario(
            username = usuario.username,
            password = usuario.hashPassword(),
            type = usuario.type
        )
        try {
            collection.insertOne(newUsuario)
        } catch (e: MongoWriteException) {
            if(e.code == 11000){
                throw IllegalStateException("Username ${usuario.username} already exists")
            }
            throw e
        }
        return newUsuario
    }

    fun getFromId(id: String): Usuario =
        collection.find(Filters.eq("_id", id)).projection(projection).first()
            ?: throw NoSuchElementException("Usuario with id $id not found")

    fun batchGetFromId(ids: Set<String>): Map<String, Usuario> {
        if (ids.isEmpty()) return emptyMap()
        return collection
            .find(Filters.`in`("_id", ids))
            .projection(projection)
            .into(HashSet()).associateBy { it.id }
    }


    fun getFromUsername(username: String): Usuario =
        collection.find(Filters.eq("username", username)).projection(projection).first()
            ?: throw InvalidCredentialsException()

    fun crearEvento(usuarioId: String, eventoId: String) {
        collection.findOneAndUpdate(Filters.eq("_id", usuarioId),
            Updates.push("eventosCreados", eventoId))
    }

    fun borrarEventoCreado(usuarioId: String, eventoId: String) {
        collection.updateOne(Filters.eq("_id", usuarioId),
            Updates.pull("eventosCreados", eventoId))
    }

    fun inscribirEvento(userId: String, id: String, confirmado: Boolean) {
        if (confirmado){
            collection.findOneAndUpdate(Filters.eq("_id", userId),
                Updates.push("eventosConfirmados", id))
        }
        else {
            collection.findOneAndUpdate(Filters.eq("_id", userId),
                Updates.push("eventosEnEspera", id))
        }
    }

    fun descinscribirEvento(userId: String, eventoId: String) {
        collection.updateOne(Filters.eq("_id", userId),
            Updates.combine(
                Updates.pull("eventosConfirmados", eventoId),
                Updates.pull("eventosEnEspera", eventoId)
            ))
    }

    fun batchDescinscribirEvento(userIds: Set<String>, eventoId: String) {
        collection.updateMany(Filters.`in`("_id", userIds),
            Updates.combine(
                Updates.pull("eventosConfirmados", eventoId),
                Updates.pull("eventosEnEspera", eventoId)
            ))
    }

    fun getEventos(userId: String): UsuarioEventos {
        return collection.withDocumentClass(UsuarioEventos::class.java)
            .find(Filters.eq("_id", userId))
            .projection(Projections.include(
                "eventosCreados",
                "eventosConfirmados",
                "eventosEnEspera")
            )
            .first() ?: throw NoSuchElementException("Usuario with id $userId not found")

    }
}
package com.g7.repo

import com.g7.evento.Evento
import com.g7.serializable.DurationCodec
import com.g7.usuario.Usuario
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider

class MongoProvider (
    connectionString: String,
    dbName: String
){
    private val pojoCodecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
        MongoClientSettings.getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
    )

    val client: MongoClient = MongoClients.create(connectionString)
    val db: MongoDatabase = client.getDatabase(dbName).withCodecRegistry(pojoCodecRegistry)

    val usuarioCollection: MongoCollection<Usuario> = db.getCollection("usuarios", Usuario::class.java)
    val eventoCollection: MongoCollection<Evento> = db.getCollection("eventos", Evento::class.java)

    fun init() {
        usuarioCollection.createIndex(
            Indexes.ascending("username"),
            IndexOptions().unique(true)
        )
    }
}
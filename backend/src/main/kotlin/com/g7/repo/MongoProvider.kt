package com.g7.repo

import com.g7.evento.Evento
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

object MongoProvider {
    lateinit var connectionString: String
    lateinit var dbName: String
    lateinit var client: MongoClient
    lateinit var db: MongoDatabase
    lateinit var usuarioCollection: MongoCollection<Usuario>
    lateinit var eventoCollection: MongoCollection<Evento>

    private val pojoCodecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
        MongoClientSettings.getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
    )

    fun init(connectionString: String, dbName: String) {
        this.connectionString = connectionString
        this.dbName = dbName
        client = MongoClients.create(connectionString)
        db = client.getDatabase(dbName).withCodecRegistry(pojoCodecRegistry)

        usuarioCollection =db.getCollection("usuarios", Usuario::class.java)
        eventoCollection = db.getCollection("eventos", Evento::class.java)
        usuarioCollection.createIndex(
            Indexes.ascending("username"),
            IndexOptions().unique(true)
        )
        val client: MongoClient = MongoClients.create(connectionString)
        client.getDatabase(dbName).withCodecRegistry(pojoCodecRegistry)
    }

}
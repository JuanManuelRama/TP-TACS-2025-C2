package com.g7.routing.eventos.id.inscriptos

import com.g7.BaseMongoTest
import com.g7.addAuth
import com.g7.application.middleware.login.JwtConfig
import com.g7.evento.Evento
import com.g7.repo.EventoRepo
import com.g7.repo.UsuarioRepo
import com.g7.usuario.Usuario
import io.ktor.client.request.*
import io.ktor.http.*
import org.bson.types.ObjectId
import kotlin.test.Test
import kotlin.test.assertEquals

class PostTest: BaseMongoTest() {
    lateinit var creator: Usuario
    lateinit var evento: Evento

    override fun populateTestData() {
        creator = UsuarioRepo.save(dataset.usuarios[0])
        evento = EventoRepo.save(creator.id, dataset.eventos[0])
    }

    @Test
    fun subscribeToEvent() = withTestApp {
        val subscriber = UsuarioRepo.save(dataset.usuarios[1])

        client.post("/eventos/${evento.id}/inscriptos") {
            addAuth(subscriber)
        }.apply {
            assertEquals(HttpStatusCode.Created, status, "Should return 201 Created")
            val inscriptos = EventoRepo.batchGetInscripcion(evento.id)

            assertEquals(1, inscriptos.size, "Event should have one subscriber")
            assertEquals(subscriber.id, inscriptos[0].usuario, "Subscriber should match")
            assertEquals("CONFIRMACION", inscriptos[0].tipo, "Subscriber should match")
            assertEquals(1, EventoRepo.getFromId(evento.id).cantInscripciones, "Event should have one subscription" )
        }
    }

    @Test
    fun waitlistToEvent() = withTestApp {
        EventoRepo.inscribirUsuario(evento.id, ObjectId())

        val subscriber = UsuarioRepo.save(dataset.usuarios[1])

        client.post("/eventos/${evento.id}/inscriptos") {
            addAuth(subscriber)
        }.apply {
            assertEquals(HttpStatusCode.Created, status, "Should return 201 Created")
            val inscriptos = EventoRepo.batchGetInscripcion(evento.id)

            assertEquals(2, inscriptos.size, "Event should have one subscriber")
            assertEquals(subscriber.id, inscriptos[1].usuario, "Subscriber should match")
            assertEquals("ESPERA", inscriptos[1].tipo, "Subscriber should match")
            assertEquals(1, EventoRepo.getFromId(evento.id).cantInscripciones, "Event should have one subscriptions" )
        }
    }


}
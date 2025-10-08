package com.g7.routing.eventos.id.inscriptos

import com.g7.BaseMongoTest
import com.g7.addAuth
import com.g7.evento.Evento
import com.g7.repo.EventoRepo
import com.g7.repo.UsuarioRepo
import com.g7.usuario.Usuario
import io.ktor.client.request.delete
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class DeleteTest: BaseMongoTest() {
    lateinit var creator: Usuario
    lateinit var evento: Evento
    lateinit var inscripto: Usuario
    lateinit var espera1: Usuario
    lateinit var espera2: Usuario

    override fun populateTestData() {
        creator = UsuarioRepo.save(dataset.usuarios[0])
        evento = EventoRepo.save(creator.id, dataset.eventos[0])
        inscripto = UsuarioRepo.save(dataset.usuarios[1])
        espera1 = UsuarioRepo.save(dataset.usuarios[2])
        espera2 = UsuarioRepo.save(dataset.usuarios[3])
        EventoRepo.inscribirUsuario(evento.id, inscripto.id)
        EventoRepo.inscribirUsuario(evento.id, espera1.id)
        EventoRepo.inscribirUsuario(evento.id, espera2.id)
    }

    @Test
    fun unsubscribeFromEvent() = withTestApp {
        client.delete("/eventos/${evento.id}/inscriptos") {
            addAuth(inscripto)
        }.apply {
            assertEquals(HttpStatusCode.NoContent, status, "Should return 204 No Content")

        }
        val inscriptos = EventoRepo.batchGetInscripto(evento.id)
        assertEquals(2, inscriptos.size, "Event should have two subscribers")
        assert(inscriptos.none { it == inscripto.id }) { "Inscripto should be removed" }
        assertEquals(espera1.id, inscriptos.inscriptos[0], "First in waitlist should be promoted to inscripto")
        assertEquals(espera2.id, inscriptos.esperas[0], "Second in waitlist should remain in waitlist")
        assertEquals(1, EventoRepo.getFromId(evento.id).cantInscripciones, "Event should have one subscription" )
        assertEquals(1, EventoRepo.getFromId(evento.id).cantEsperaExitosas, "Event should have one successful waitlist promotion" )
    }

    @Test
    fun quitWaitlist() = withTestApp {
        client.delete("/eventos/${evento.id}/inscriptos") {
            addAuth(espera1)
        }.apply {
            assertEquals(HttpStatusCode.NoContent, status, "Should return 204 No Content")
        }
        val inscriptos = EventoRepo.batchGetInscripto(evento.id)

        assertEquals(2, inscriptos.size, "Event should have two subscribers")
        assertEquals(1, inscriptos.inscriptos.size, "There should be one confirmed subscriber")
        assert(inscriptos.none { it == espera1.id }) { "Waitlisted should be removed" }
        assertEquals(inscripto.id, inscriptos.inscriptos[0], "Inscripto should remain inscripto")
        assertEquals(espera2.id, inscriptos.esperas[0], "Second in waitlist should remain in waitlist")
        assertEquals(1, EventoRepo.getFromId(evento.id).cantInscripciones, "Event should have one subscription" )
        assertEquals(1, EventoRepo.getFromId(evento.id).cantEsperaCancelada, "Event should have one failed waitlist" )
    }

    @Test
    fun unsubscribedUserCannotUnsubscribe() = withTestApp {
        EventoRepo.cancelarInscripcion(evento.id, espera2.id)
        client.delete("/eventos/${evento.id}/inscriptos") {
            addAuth(espera2)
        }.apply {
            assertEquals(HttpStatusCode.NotFound, status, "Should return 404 Not Found")
        }
        val inscriptos = EventoRepo.batchGetInscripto(evento.id)

        assertEquals(2, inscriptos.size, "Event should still have three subscribers")
    }
}

package com.g7.routing.eventos.id.inscriptos

import com.g7.BaseMongoTest
import com.g7.addAuth
import com.g7.evento.Evento
import com.g7.evento.InscripcionDto
import com.g7.repo.EventoRepo
import com.g7.repo.UsuarioRepo
import com.g7.usuario.Usuario
import com.g7.usuario.dto.toResponseDto
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTest: BaseMongoTest() {
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
    fun getInscriptos() = withTestApp {
        client.get("/eventos/${evento.id}/inscriptos") {
            addAuth(creator)
        }.apply {
            assertEquals(io.ktor.http.HttpStatusCode.OK, status, "Should return 200 OK")
            val inscriptos = Json.decodeFromString<List<InscripcionDto>>(bodyAsText())

            assertEquals(3, inscriptos.size, "Event should have three subscribers")
            assertEquals(inscripto.toResponseDto(), inscriptos[0].usuario, "First subscriber should match")
            assertEquals("CONFIRMACION", inscriptos[0].tipo, "First subscriber type should match")
            assertEquals(espera1.toResponseDto(), inscriptos[1].usuario, "Second subscriber should match")
            assertEquals("ESPERA", inscriptos[1].tipo, "Second subscriber type should match")
            assertEquals(espera2.toResponseDto(), inscriptos[2].usuario, "Third subscriber should match")
            assertEquals("ESPERA", inscriptos[2].tipo, "Third subscriber type should match")
        }
    }

    @Test
    fun onlyCreatorCanGetInscriptos() = withTestApp {
        client.get("/eventos/${evento.id}/inscriptos") {
            addAuth(inscripto)
        }.apply {
            assertEquals(io.ktor.http.HttpStatusCode.Forbidden, status, "Should return 403 Forbidden")
        }
    }

    @Test
    fun getInscriptosUnauthorized() = withTestApp {
        client.get("/eventos/${evento.id}/inscriptos").apply {
            assertEquals(io.ktor.http.HttpStatusCode.Unauthorized, status, "Should return 401 Unauthorized")
        }
    }
}
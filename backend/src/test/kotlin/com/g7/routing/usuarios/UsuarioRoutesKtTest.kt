package com.g7.routing.usuarios

import com.g7.BaseMongoTest
import com.g7.repo.UsuarioRepo
import com.g7.setupTestApplication
import com.g7.usuario.dto.LoginResponseDto
import com.g7.usuario.dto.UsuarioInputDto
import com.g7.usuario.dto.UsuarioResponseDto
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UsuarioRoutesKtTest: BaseMongoTest() {
    val inputUser = dataset.usuarios[0]

    @Test  @Order(1)
    fun usuarioIsCreated() = withTestApp {
        client.post("/usuarios") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(inputUser))
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            val responseBody = bodyAsText()
            val createdUser = Json.decodeFromString<UsuarioResponseDto>(responseBody)

            assertEquals(createdUser.username, inputUser.username, "Returned username should match input")

            val fetchedFromDb = UsuarioRepo.getFromId(createdUser.id)
            assertEquals(inputUser.username, fetchedFromDb.username, "DB username should match input")
            assertEquals(createdUser.id, fetchedFromDb.id, "DB id should match response id")
            assertNotEquals((inputUser.password), fetchedFromDb.password, "Stored password should be hashed")
        }
    }

    @Test @Order(2)
    fun usuarioWithSameNameIsRejected() = withTestApp {
        client.post("/usuarios") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(inputUser.copy(password = "wrong"))) // Change password to ensure username is the conflict
        }.apply {
            assertEquals(HttpStatusCode.Conflict, status)
            assertEquals(UsuarioRepo.getUsuarios().size, 1, "Only one user gets created")
        }

    }

    @Test @Order(3)
    fun loginWorks() = withTestApp {
        client.post("/usuarios/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username": "${inputUser.username}", "password": "${inputUser.password}"}""")
        }.apply {
            assertEquals(HttpStatusCode.OK,status)
            val loginResponse = Json.decodeFromString<LoginResponseDto>(bodyAsText())
            assertTrue(loginResponse.token.isNotBlank(), "Token should not be blank")
        }

    }

    @Order(4)
    @Test
    fun wrongLoginIsRejected() = withTestApp {
        client.post("/usuarios/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username": "${inputUser.username}", "password": "WRONG"}""")
        }.apply{
            assertEquals(HttpStatusCode.Unauthorized, status)
        }
    }

    @Order(5)
    @Test
    fun loginDoesntSayWhetherUsernameOrPasswordIsWrong() = withTestApp {
        val response1 = client.post("/usuarios/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username": "WRONG", "password": "${inputUser.password}"}""")
        }
        val response2 = client.post("/usuarios/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username": "${inputUser.username}", "password": "WRONG"}""")
        }
        assertEquals(response1.status, response2.status, "Statuses should match")
        assertEquals(response1.bodyAsText(), response2.bodyAsText(), "Response bodies should match")
    }
}


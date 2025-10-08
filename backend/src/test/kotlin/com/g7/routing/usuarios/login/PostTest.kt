package com.g7.routing.usuarios.login

import com.g7.BaseMongoTest
import com.g7.repo.UsuarioRepo
import com.g7.usuario.LoginResponseDto
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PostTest: BaseMongoTest() {
    val inputUser = dataset.usuarios[0]

    override fun populateTestData() {
        UsuarioRepo.save(inputUser)
    }

    @Test
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

    @Test
    fun wrongLoginIsRejected() = withTestApp {
        client.post("/usuarios/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username": "${inputUser.username}", "password": "WRONG"}""")
        }.apply{
            assertEquals(HttpStatusCode.Unauthorized, status)
        }
    }

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
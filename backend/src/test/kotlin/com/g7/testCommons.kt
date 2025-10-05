package com.g7

import com.g7.evento.EventoInputDto
import com.g7.repo.MongoProvider
import com.g7.application.infra.configureDb
import com.g7.application.configureRouting
import com.g7.application.middleware.installAuth
import com.g7.application.middleware.installContentNegotiation
import com.g7.application.middleware.installStatusPages
import com.g7.application.middleware.login.JwtConfig
import com.g7.usuario.Usuario
import com.g7.usuario.dto.UsuarioInputDto
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

val testConfig = MapApplicationConfig(
    "jwt.secret" to "super-secret-test",
    "jwt.issuer" to "test-issuer",
    "jwt.audience" to "test-audience",
    "jwt.validity" to "3600000",
    "jwt.claims.userId" to "userId",
    "jwt.claims.username" to "username",
    "jwt.claims.type" to "type"
)

fun Application.setupTestApplication() {
    installAuth(testConfig)
    installContentNegotiation()
    installStatusPages()
    configureRouting()
}

@Serializable
data class Dataset(
    val usuarios: List<UsuarioInputDto>,
    val eventos: List<EventoInputDto>
)

@Container
val mongoContainer: MongoDBContainer = MongoDBContainer("mongo:8.0.14")

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseMongoTest {
    val dataset = Json.decodeFromString<Dataset>(
        Thread.currentThread().contextClassLoader.getResource("dataset.json")!!.readText())

    open fun populateTestData(){
    }

    @BeforeAll
    fun setup() {
        mongoContainer.start()
    }

    @AfterEach
    fun cleanDb() {
        MongoProvider.client.getDatabase("test").drop()
    }

    @AfterAll
    fun tearDown() {
        MongoProvider.client.getDatabase("test").drop()
        MongoProvider.client.close()
    }

    protected fun withTestApp(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        externalServices {
            configureDb(MapApplicationConfig(
                "mongo.uri" to mongoContainer.connectionString,
                "mongo.db" to "test"))
            JwtConfig.init(testConfig)
        }
        application {
            setupTestApplication()
        }
        populateTestData()
        block()
    }
}

fun HttpRequestBuilder.addAuth(user: Usuario) {
    this.headers.append("Authorization", "Bearer ${JwtConfig.generateToken(user)}")
}
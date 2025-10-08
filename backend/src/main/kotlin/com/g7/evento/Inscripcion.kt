package com.g7.evento

import com.g7.repo.UsuarioRepo
import com.g7.serializable.LocalDateTimeSerializer
import com.g7.usuario.Usuario
import com.g7.usuario.UsuarioResponseDto
import com.g7.usuario.toResponseDto
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.time.LocalDateTime

sealed class Inscripcion {
    abstract val usuario: ObjectId
    abstract val horaInscripcion: LocalDateTime
    abstract val confirmado: Boolean
    abstract val tipo: String

    data class Confirmada(
        override val usuario: ObjectId,
        override val horaInscripcion: LocalDateTime,
        val horaConfirmacion: LocalDateTime
    ) : Inscripcion() {
        override val confirmado: Boolean = true
        override val tipo: String = "CONFIRMACION"
    }

    data class Espera(
        override val usuario: ObjectId,
        override val horaInscripcion: LocalDateTime
    ) : Inscripcion() {
        override val confirmado: Boolean = false
        override val tipo: String = "ESPERA"

        fun confirmar() = Confirmada(
            usuario = this.usuario,
            horaInscripcion = this.horaInscripcion,
            horaConfirmacion = LocalDateTime.now()
        )
    }
}

@Serializable
data class InscripcionDto(
    val usuario: UsuarioResponseDto,
    @Serializable(with = LocalDateTimeSerializer::class)
    val horaInscripcion: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val horaConfirmacion: LocalDateTime? = null,
    val tipo: String
)

fun Inscripcion.toDto(repo: UsuarioRepo) = toDto(repo.getFromId(this.usuario).toResponseDto())

fun Inscripcion.toDto(usuario: UsuarioResponseDto): InscripcionDto {
    val horaConfirmacion = when (this) {
        is Inscripcion.Espera -> null
        is Inscripcion.Confirmada -> this.horaConfirmacion
    }
    return InscripcionDto(
        usuario =usuario,
        horaInscripcion = this.horaInscripcion,
        horaConfirmacion = horaConfirmacion,
        tipo = this.tipo
    )
}

data class Inscriptos (
    val inscriptos: List<ObjectId> = emptyList(),
    val esperas: List<ObjectId> = emptyList()
) {
    val size get() = inscriptos.size + esperas.size
    fun isEmpty() = inscriptos.isEmpty() && esperas.isEmpty()
    fun none(predicate: (ObjectId) -> Boolean) = inscriptos.none(predicate) && esperas.none(predicate)
}

@Serializable
data class InscriptosDto (
    val inscriptos: List<UsuarioResponseDto>,
    val esperas: List<UsuarioResponseDto>
)

fun Inscriptos.toDto (map: Map<ObjectId, Usuario>): InscriptosDto {
    return InscriptosDto(
        inscriptos = this.inscriptos.map { map[it]!!.toResponseDto() },
        esperas = this.esperas.map { map[it]!!.toResponseDto() }
    )
}



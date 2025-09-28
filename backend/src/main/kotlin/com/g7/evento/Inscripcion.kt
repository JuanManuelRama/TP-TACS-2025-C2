package com.g7.evento

import com.g7.repo.UsuarioRepo
import com.g7.serializable.LocalDateTimeSerializer
import com.g7.usuario.Usuario
import com.g7.usuario.dto.UsuarioResponseDto
import com.g7.usuario.dto.toResponseDto
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.time.LocalDateTime

/*
data class Inscripcion (
    val usuario: ObjectId,
    val horaInscripcion: LocalDateTime,
    val horaConfirmacion: LocalDateTime? = null,
) {
    constructor(usuario: ObjectId, horaInscripcion: LocalDateTime, smth:Any?): this(
        usuario,
        horaInscripcion,
        null
    )
    fun confirmado() = horaConfirmacion != null
    fun confirmar() = this.copy(horaConfirmacion = LocalDateTime.now())
}*/

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


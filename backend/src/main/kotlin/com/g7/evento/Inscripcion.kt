package com.g7.evento

import com.g7.serializable.LocalDateTimeSerializer
import com.g7.serializable.UUIDSerializer
import com.g7.usuario.Usuario
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

sealed class Inscripcion {
    data class Confirmacion(
        val usuario: Usuario,
        val horaInscripcion: LocalDateTime,
        val espera: Duration?,
    ): Inscripcion()

    data class Espera(
        val usuario: Usuario,
        val horaInscripcion: LocalDateTime
    ): Inscripcion() {
        fun tiempoEsperando(): Duration =
            java.time.Duration.between(this.horaInscripcion, LocalDateTime.now()).toKotlinDuration()
        fun toConfirmacion(): Confirmacion = Confirmacion(
            usuario = this.usuario,
            horaInscripcion = this.horaInscripcion,
            espera = this.tiempoEsperando())
    }
}

@Serializable
data class InscripcionDto(
    @Serializable(with = UUIDSerializer::class)
    val usuarioId: UUID,
    @Serializable(with = LocalDateTimeSerializer::class)
    val horaInscripcion: LocalDateTime,
    val espera: Duration? = null,
    val tipo: String
)

fun Inscripcion.toDto(): InscripcionDto = when (this) {
    is Inscripcion.Confirmacion -> InscripcionDto(
        usuarioId = usuario.id,
        horaInscripcion = horaInscripcion,
        espera = espera,
        tipo = "CONFIRMACION"
    )
    is Inscripcion.Espera -> InscripcionDto(
        usuarioId = usuario.id,
        horaInscripcion = horaInscripcion,
        espera = null,
        tipo = "ESPERA"
    )
}
package com.g7.evento

import com.g7.serializable.KotlinDurationSerializer
import com.g7.serializable.LocalDateTimeSerializer
import com.g7.usuario.Usuario
import com.g7.usuario.UsuarioDto
import com.g7.usuario.toDto
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Duration

@Serializable
data class EventoDto (
    val organizador: UsuarioDto,
    val titulo: String,
    val descripcion: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val inicio: LocalDateTime,
    @Serializable(with = KotlinDurationSerializer::class)
    val duracion: Duration,
    val cupoMaximo: Int,
    val cupoMinimio: Int?,
    val precio: Float,
    val categorias: List<Categoria>,
    )

fun Evento.toDto(): EventoDto = EventoDto(
    organizador = this.organizador.toDto(),
    titulo = this.titulo,
    descripcion = this.descripcion,
    inicio = this.inicio,
    duracion = this.duracion,
    cupoMaximo = this.cupoMaximo,
    cupoMinimio = this.cupoMinimio,
    precio = this.precio,
    categorias = this.categorias
)

fun EventoDto.toDomain(): Evento = Evento(
    id = UUID.randomUUID(),
    organizador = Usuario("juan"),
    titulo = this.titulo,
    descripcion = this.descripcion,
    inicio = this.inicio,
    duracion = this.duracion,    cupoMaximo = this.cupoMaximo,
    cupoMinimio = this.cupoMinimio,
    precio = this.precio,
    categorias = this.categorias
)

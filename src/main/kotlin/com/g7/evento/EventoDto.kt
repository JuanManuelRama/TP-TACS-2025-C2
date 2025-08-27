package com.g7.evento

import com.g7.repo.UsuarioRepository
import com.g7.serializable.KotlinDurationSerializer
import com.g7.serializable.LocalDateTimeSerializer
import com.g7.serializable.UUIDSerializer
import com.g7.usuario.Usuario
import com.g7.usuario.UsuarioDto
import com.g7.usuario.toDto
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Duration

@Serializable
data class EventoDto (
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,
    @Serializable(with = UUIDSerializer::class)
    val organizador: UUID,
    val titulo: String,
    val descripcion: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val inicio: LocalDateTime,
    @Serializable(with = KotlinDurationSerializer::class)
    val duracion: Duration,
    val cupoMaximo: Int,
    val cupoMinimio: Int? = null,
    val precio: Float,
    val categorias: List<Categoria> = ArrayList(),
    )

fun Evento.toDto(): EventoDto = EventoDto(
    id = this.id,
    organizador = this.organizador.id,
    titulo = this.titulo,
    descripcion = this.descripcion,
    inicio = this.inicio,
    duracion = this.duracion,
    cupoMaximo = this.cupoMaximo,
    cupoMinimio = this.cupoMinimio,
    precio = this.precio,
    categorias = this.categorias
)

/**
 * Convierte un [EventoDto] en un objeto de dominio [Evento].
 *
 * La conversión valida primero que el DTO tenga un `id`.
 * Si no tiene, retorna un [Result.failure] con una [RuntimeException].
 *
 * Luego, intenta obtener el organizador correspondiente mediante
 * [UsuarioRepository.getUsuarioFromId]. Si el usuario existe, construye
 * un [Evento] con todos los datos del DTO; de lo contrario, propaga el
 * error del [Result].
 *
 * @return un [Result] que contiene el [Evento] convertido si la operación
 *         fue exitosa, o un [Result.failure] en caso de que falte el `id`
 *         o no se pueda obtener el organizador.
 */
fun EventoDto.toDomain(): Result<Evento> {
    if (this.id == null) {
        return Result.failure(RuntimeException("No se puede convertir a dominio sin id de evento"))
    }
    return UsuarioRepository.getUsuarioFromId(this.organizador)
        .map { organizador ->
            Evento(
                id = this.id,
                organizador = organizador,
                titulo = this.titulo,
                descripcion = this.descripcion,
                inicio = this.inicio,
                duracion = this.duracion,
                cupoMaximo = this.cupoMaximo,
                cupoMinimio = this.cupoMinimio,
                precio = this.precio,
                categorias = this.categorias
            )
        }
}


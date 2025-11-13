package Model

import kotlinx.serialization.Serializable

@Serializable
data class UsuarioLogeado(
    val usuario: Usuario,
    val roles: List<String>,
    val colorCasa: String
)


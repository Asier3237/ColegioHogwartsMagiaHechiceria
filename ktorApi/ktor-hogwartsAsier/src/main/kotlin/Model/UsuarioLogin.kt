package Model

import kotlinx.serialization.Serializable

@Serializable
data class UsuarioLogin(val nombre:String, val pwd:String)

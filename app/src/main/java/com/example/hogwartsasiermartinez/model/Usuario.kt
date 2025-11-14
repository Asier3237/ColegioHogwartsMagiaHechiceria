package com.example.hogwartsasiermartinez.model

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: Int? = null,
    val nombre: String,
    val password: String,
    val experiencia: Int? = null,
    val nivel: Int? = null,
    val casa_id: Int
)

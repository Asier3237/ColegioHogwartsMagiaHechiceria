package com.example.hogwartsasiermartinez.model

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: Int? = null,
    val nombre: String,
    val password: String,
    val experiencia: Int,
    val nivel: Int,
    val casa_id: Int
)

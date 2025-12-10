package com.example.Model

import kotlinx.serialization.Serializable

@Serializable
data class Asignatura(
    val id: Int? = null,
    val nombre: String
)

package com.example.Model

import kotlinx.serialization.Serializable

@Serializable
data class Pocima(
    val id: Int,
    val nombre: String,
    val resumen: String,
    val creador_id: Int,
    val validada: Int, // 0=pendiente, 1=validada, 2=rechazada
    val tipo: String // "buena" o "mala"
)

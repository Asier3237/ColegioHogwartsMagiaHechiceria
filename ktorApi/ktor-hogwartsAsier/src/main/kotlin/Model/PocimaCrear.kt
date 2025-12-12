package com.example.Model

import kotlinx.serialization.Serializable

@Serializable
data class PocimaCrear(
    val nombre: String,
    val resumen: String,
    val creadorId: Int,
    val ingredientes: List<IngredientePocima>
)

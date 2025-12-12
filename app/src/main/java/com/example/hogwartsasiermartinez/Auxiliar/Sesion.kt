package com.example.hogwartsasiermartinez.Auxiliar

//object auxiliar para poder pasar datos desde una activity a otra
object Sesion {
    var usuarioId: Int = -1
    var rolActivo: String = ""
    lateinit var roles: List<String>
    var casaId: Int = -1
    var asignaturaId: Int = -1
    var colorCasa: String = "#3C1F0E"
}

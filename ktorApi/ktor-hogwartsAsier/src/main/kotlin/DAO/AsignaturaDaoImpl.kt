package com.example.DAO

import Database.Conexion
import com.example.Model.Asignatura

object AsignaturaDaoImpl {

    fun obtenerTodas(): List<Asignatura> {
        val asignaturas = mutableListOf<Asignatura>()
        val query = "SELECT * FROM asignatura"
        val connection = Conexion.getConnection()
        if (connection != null) {
            val stmt = connection.prepareStatement(query)
            val rs = stmt.executeQuery()
            while (rs.next()) {
                asignaturas.add(
                    Asignatura(
                        rs.getInt("id"),
                        rs.getString("nombre")
                    )
                )
            }
            rs.close()
            stmt.close()
            connection.close()
        }
        return asignaturas
    }
}
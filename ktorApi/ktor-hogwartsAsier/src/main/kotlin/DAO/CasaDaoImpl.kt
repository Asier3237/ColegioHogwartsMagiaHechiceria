package com.example.DAO

import Database.Conexion
import com.example.Model.Casa

object CasaDaoImpl {

    fun obtenerTodas(): List<Casa> {
        val casas = mutableListOf<Casa>()
        val query = "SELECT id, nombre, puntos FROM casa"
        val connection = Conexion.getConnection()
        if (connection != null) {
            val stmt = connection.prepareStatement(query)
            val rs = stmt.executeQuery()
            while (rs.next()) {
                casas.add(
                    Casa(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("puntos")
                    )
                )
            }
            rs.close()
            stmt.close()
            connection.close()
        }
        return casas
    }


}
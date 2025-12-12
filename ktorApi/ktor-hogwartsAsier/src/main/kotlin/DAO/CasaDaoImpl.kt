package com.example.DAO

import Database.Conexion
import com.example.Model.Casa
import java.sql.SQLException

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

    fun getCasasRanking(): List<Casa> {
        val query = "SELECT * FROM casa ORDER BY puntos DESC"
        val connection = Conexion.getConnection() ?: return emptyList()
        val casas = mutableListOf<Casa>()
        try {
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                casas.add(
                    Casa(
                        id = resultSet.getInt("id"),
                        nombre = resultSet.getString("nombre"),
                        puntos = resultSet.getInt("puntos")
                    )
                )
            }
        } catch (e: SQLException) {
            println("Error en BD al obtener ranking de casas: ${e.message}")
        } finally {
            connection.close()
        }
        return casas
    }

}
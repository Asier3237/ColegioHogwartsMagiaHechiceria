package com.example.DAO

import Database.Conexion
import com.example.Model.Ingrediente
import com.example.Model.Pocima
import com.example.Model.PocimaCrear
import java.sql.SQLException

object PocimasDaoImpl {

    fun getPociones(rol: String, usuarioId: Int): List<Pocima> {
        val query = when (rol) {
            "alumno" -> "SELECT * FROM pocimas WHERE creador_id = ? AND fecha_borrado IS NULL"
            "profesor" -> "SELECT * FROM pocimas WHERE validada = 0 AND fecha_borrado IS NULL"
            "admin" -> "SELECT * FROM pocimas WHERE fecha_borrado IS NULL"
            else -> return emptyList()
        }

        val connection = Conexion.getConnection() ?: return emptyList()
        val pociones = mutableListOf<Pocima>()
        try {
            val statement = connection.prepareStatement(query)
            if (rol == "alumno") {
                statement.setInt(1, usuarioId)
            }
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                pociones.add(
                    Pocima(
                        id = resultSet.getInt("id"),
                        nombre = resultSet.getString("nombre"),
                        resumen = resultSet.getString("resumen"),
                        creador_id = resultSet.getInt("creador_id"),
                        validada = resultSet.getInt("validada"),
                        tipo = resultSet.getString("tipo")
                    )
                )
            }
        } catch (e: SQLException) {
            println("Error en BD al obtener pociones: ${e.message}")
        } finally {
            connection.close()
        }
        return pociones
    }

    fun getIngredientes(): List<Ingrediente> {
        val query = "SELECT * FROM ingrediente"
        val connection = Conexion.getConnection() ?: return emptyList()
        val ingredientes = mutableListOf<Ingrediente>()
        try {
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                ingredientes.add(
                    Ingrediente(
                        id = resultSet.getInt("id"),
                        nombre = resultSet.getString("nombre"),
                        analgesia = resultSet.getInt("analgesia"),
                        curativo = resultSet.getInt("curativo"),
                        desinflamatorio = resultSet.getInt("desinflamatorio"),
                        sanacion = resultSet.getInt("sanacion")
                    )
                )
            }
        } catch (e: SQLException) {
            println("Error en BD al obtener ingredientes: ${e.message}")
        } finally {
            connection.close()
        }
        return ingredientes
    }

    fun crearPocion(pocimaData: PocimaCrear): Boolean {
        val connection = Conexion.getConnection() ?: return false
        connection.autoCommit = false

        try {
            var sumaValores = 0
            for (ingredientePocima in pocimaData.ingredientes) {
                val ingQuery = "SELECT (analgesia + curativo + desinflamatorio + sanacion) as total_valor FROM ingrediente WHERE id = ?"
                val ingStatement = connection.prepareStatement(ingQuery)
                ingStatement.setInt(1, ingredientePocima.ingredienteId)
                val ingResult = ingStatement.executeQuery()
                if (ingResult.next()) {
                    sumaValores += ingResult.getInt("total_valor") * ingredientePocima.cantidad
                }
                ingStatement.close()
            }
            val tipoPocion = if (sumaValores >= 0) "buena" else "mala"

            val pocimaQuery = "INSERT INTO pocimas (nombre, resumen, creador_id, tipo) VALUES (?, ?, ?, ?)"
            val pocimaStatement = connection.prepareStatement(pocimaQuery, java.sql.Statement.RETURN_GENERATED_KEYS)
            pocimaStatement.setString(1, pocimaData.nombre)
            pocimaStatement.setString(2, pocimaData.resumen)
            pocimaStatement.setInt(3, pocimaData.creadorId)
            pocimaStatement.setString(4, tipoPocion)
            pocimaStatement.executeUpdate()

            val generatedKeys = pocimaStatement.generatedKeys
            if (!generatedKeys.next()) {
                connection.rollback()
                return false
            }
            val nuevaPocimaId = generatedKeys.getInt(1)
            pocimaStatement.close()

            val pocimaIngredienteQuery = "INSERT INTO pocima_ingrediente (pocima_id, ingrediente_id, cantidad) VALUES (?, ?, ?)"
            for (ingredientePocima in pocimaData.ingredientes) {
                val piStatement = connection.prepareStatement(pocimaIngredienteQuery)
                piStatement.setInt(1, nuevaPocimaId)
                piStatement.setInt(2, ingredientePocima.ingredienteId)
                piStatement.setInt(3, ingredientePocima.cantidad)
                piStatement.executeUpdate()
                piStatement.close()
            }

            val casaQuery = "SELECT casa_id FROM usuario WHERE id = ?"
            val casaStatement = connection.prepareStatement(casaQuery)
            casaStatement.setInt(1, pocimaData.creadorId)
            val casaResult = casaStatement.executeQuery()
            if (!casaResult.next()) {
                println("No se encontró la casa del usuario.")
                connection.rollback()
                return false
            }
            val casaId = casaResult.getInt("casa_id")
            casaStatement.close()

            val updateUserQuery = "UPDATE usuario SET experiencia = experiencia + 2 WHERE id = ?"
            val updateUserStatement = connection.prepareStatement(updateUserQuery)
            updateUserStatement.setInt(1, pocimaData.creadorId)
            updateUserStatement.executeUpdate()
            updateUserStatement.close()

            val updateCasaQuery = "UPDATE casa SET puntos = puntos + 2 WHERE id = ?"
            val updateCasaStatement = connection.prepareStatement(updateCasaQuery)
            updateCasaStatement.setInt(1, casaId)
            updateCasaStatement.executeUpdate()
            updateCasaStatement.close()

            connection.commit()
            return true

        } catch (e: SQLException) {
            println("Error en BD al crear poción: ${e.message}")
            e.printStackTrace()
            connection.rollback()
            return false
        } finally {
            connection.autoCommit = true
            connection.close()
        }
    }


    fun validarPocion(pocionId: Int, nuevoEstado: Int): Boolean {
        val query = "UPDATE pocimas SET validada = ?, fecha_modificacion = NOW() WHERE id = ?"
        val connection = Conexion.getConnection() ?: return false
        return try {
            val statement = connection.prepareStatement(query)
            statement.setInt(1, nuevoEstado)
            statement.setInt(2, pocionId)
            statement.executeUpdate() > 0
        } catch (e: SQLException) {
            println("Error en BD al validar poción: ${e.message}")
            false
        } finally {
            connection.close()
        }
    }

    fun borrarPocion(pocionId: Int): Boolean {
        val query = "UPDATE pocimas SET fecha_borrado = NOW() WHERE id = ?"
        val connection = Conexion.getConnection() ?: return false
        return try {
            val statement = connection.prepareStatement(query)
            statement.setInt(1, pocionId)
            statement.executeUpdate() > 0
        } catch (e: SQLException) {
            println("Error en BD al borrar poción: ${e.message}")
            false
        } finally {
            connection.close()
        }
    }


}
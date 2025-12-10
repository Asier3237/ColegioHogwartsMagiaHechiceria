package com.example.DAO

import Database.Conexion
import com.example.Model.Hechizo
import java.sql.SQLException

object HechizoDaoImpl {

    // --- FUNCIÓN PARA OBTENER TODOS LOS HECHIZOS ---
    fun getTodosLosHechizos(): List<Hechizo> {
        val query = "SELECT * FROM hechizo"
        val connection = Conexion.getConnection() ?: return emptyList()
        val hechizos = mutableListOf<Hechizo>()
        try {
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                hechizos.add(
                    Hechizo(
                        id = resultSet.getInt("id"),
                        nombre = resultSet.getString("nombre"),
                        descripcion = resultSet.getString("descripcion"),
                        experiencia = resultSet.getInt("experiencia")
                    )
                )
            }
            statement.close()
        } catch (e: SQLException) {
            println("Error en BD al obtener hechizos: ${e.message}")
        } finally {
            connection.close()
        }
        return hechizos
    }

    // --- FUNCIÓN PARA CREAR UN NUEVO HECHIZO ---
    fun crearHechizo(nombre: String, descripcion: String, experiencia: Int): Boolean {
        val query = "INSERT INTO hechizo (nombre, descripcion, experiencia) VALUES (?, ?, ?)"
        val connection = Conexion.getConnection() ?: return false
        return try {
            val statement = connection.prepareStatement(query)
            statement.setString(1, nombre)
            statement.setString(2, descripcion)
            statement.setInt(3, experiencia)
            val affectedRows = statement.executeUpdate()
            statement.close()
            affectedRows > 0
        } catch (e: SQLException) {
            println("Error en BD al crear hechizo: ${e.message}")
            false
        } finally {
            connection.close()
        }
    }

    // --- FUNCIÓN PARA QUE UN ALUMNO APRENDA UN HECHIZO ---
    fun aprenderHechizo(alumnoId: Int, hechizoId: Int): Boolean {
        val connection = Conexion.getConnection() ?: return false
        // Iniciamos una transacción. O se hace todo, o no se hace nada.
        connection.autoCommit = false

        try {
            // --- Paso 1: Comprobar si el alumno ya sabe el hechizo ---
            val checkQuery = "SELECT COUNT(*) FROM alumno_hechizo WHERE alumno_id = ? AND hechizo_id = ?"
            val checkStatement = connection.prepareStatement(checkQuery)
            checkStatement.setInt(1, alumnoId)
            checkStatement.setInt(2, hechizoId)
            val resultSet = checkStatement.executeQuery()
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                println("El alumno ya conoce este hechizo. No se suman puntos.")
                connection.rollback() // Cancelamos la transacción
                return true // Devolvemos 'true' porque la operación no falló, simplemente no hizo nada.
            }
            checkStatement.close()

            // --- Paso 2: Obtener la experiencia del hechizo y la casa del alumno ---
            val dataQuery = "SELECT h.experiencia, u.casa_id FROM hechizo h, usuario u WHERE h.id = ? AND u.id = ?"
            val dataStatement = connection.prepareStatement(dataQuery)
            dataStatement.setInt(1, hechizoId)
            dataStatement.setInt(2, alumnoId)
            val dataResult = dataStatement.executeQuery()

            if (!dataResult.next()) {
                println("No se encontró el hechizo o el usuario.")
                connection.rollback()
                return false
            }
            val experienciaGanada = dataResult.getInt("experiencia")
            val casaId = dataResult.getInt("casa_id")
            dataStatement.close()

            // --- Paso 3: Registrar que el alumno ha aprendido el hechizo ---
            val insertQuery = "INSERT INTO alumno_hechizo (alumno_id, hechizo_id, fecha_aprendizaje) VALUES (?, ?, NOW())"
            val insertStatement = connection.prepareStatement(insertQuery)
            insertStatement.setInt(1, alumnoId)
            insertStatement.setInt(2, hechizoId)
            insertStatement.executeUpdate()
            insertStatement.close()

            // --- Paso 4: Actualizar la experiencia del propio alumno ---
            val updateUserQuery = "UPDATE usuario SET experiencia = experiencia + ? WHERE id = ?"
            val updateUserStatement = connection.prepareStatement(updateUserQuery)
            updateUserStatement.setInt(1, experienciaGanada)
            updateUserStatement.setInt(2, alumnoId)
            updateUserStatement.executeUpdate()
            updateUserStatement.close()

            // --- Paso 5: Actualizar los puntos de la casa del alumno ---
            val updateCasaQuery = "UPDATE casa SET puntos = puntos + ? WHERE id = ?"
            val updateCasaStatement = connection.prepareStatement(updateCasaQuery)
            updateCasaStatement.setInt(1, experienciaGanada)
            updateCasaStatement.setInt(2, casaId)
            updateCasaStatement.executeUpdate()
            updateCasaStatement.close()

            // --- Finalizar ---
            connection.commit() // Si todo ha ido bien, confirmamos todos los cambios.
            println("Éxito: Alumno $alumnoId aprendió hechizo $hechizoId. Se sumaron $experienciaGanada puntos a él y a su casa.")
            return true

        } catch (e: SQLException) {
            println("Error en BD al aprender hechizo: ${e.message}")
            connection.rollback() // Si algo falla, revertimos todos los cambios.
            return false
        } finally {
            connection.autoCommit = true // Devolvemos la conexión a su estado normal.
            connection.close()
        }
    }

    fun borrarHechizo(hechizoId: Int): Boolean {
        val query = "DELETE FROM hechizo WHERE id = ?"
        val connection = Conexion.getConnection() ?: return false
        return try {
            val statement = connection.prepareStatement(query)
            statement.setInt(1, hechizoId)
            val affectedRows = statement.executeUpdate()
            statement.close()
            affectedRows > 0 // Devuelve 'true' si se borró al menos una fila
        } catch (e: SQLException) {
            println("Error en BD al borrar hechizo: ${e.message}")
            false
        } finally {
            connection.close()
        }
    }

}
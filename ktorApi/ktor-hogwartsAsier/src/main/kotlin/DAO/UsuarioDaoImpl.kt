package DAO

import Database.Conexion
import Model.Usuario
import Model.UsuarioLogeado
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

object UsuarioDaoImpl {

    fun obtenerTodos(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val query = "SELECT * FROM usuario"

        val connection = Conexion.getConnection()
        if (connection != null) {
            try {
                val statement = connection.prepareStatement(query)
                val result = statement.executeQuery()

                while (result.next()) {
                    lista.add(result.toUsuario())
                }

                result.close()
                statement.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                connection.close()
            }
        } else {
            println("Error: No se pudo establecer conexión con la base de datos.")
        }

        return lista
    }

    fun registrar(usuario: Usuario): Boolean {
        val existeQuery = "SELECT COUNT(*) FROM usuario WHERE nombre = ?"
        val insertQuery = """
            INSERT INTO usuario (nombre, password, experiencia, nivel, casa_id)
            VALUES (?, ?, ?, ?, ?)
            """.trimIndent()

        val connection = Conexion.getConnection()
        if (connection != null) {
            try {
                // Comprobar si ya existe
                val checkStmt = connection.prepareStatement(existeQuery)
                checkStmt.setString(1, usuario.nombre)
                val result = checkStmt.executeQuery()
                result.next()
                val yaExiste = result.getInt(1) > 0
                result.close()
                checkStmt.close()

                if (yaExiste) {
                    println("El usuario '${usuario.nombre}' ya está registrado.")
                    return false
                }

                // Insertar usuario y recuperar ID generado
                val insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)
                insertStmt.setString(1, usuario.nombre)
                insertStmt.setString(2, usuario.password)
                insertStmt.setInt(3, usuario.experiencia ?: 0)
                insertStmt.setInt(4, usuario.nivel ?: 1)
                insertStmt.setInt(5, usuario.casa_id)

                val filas = insertStmt.executeUpdate()

                if (filas > 0) {
                    val keys = insertStmt.generatedKeys
                    if (keys.next()) {
                        val usuarioId = keys.getInt(1)

                        // Asignar rol por defecto (alumno)
                        val statementRol = connection.prepareStatement(
                            "INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (?, ?)"
                        )
                        statementRol.setInt(1, usuarioId)
                        statementRol.setInt(2, 1) // rol alumno
                        statementRol.executeUpdate()
                        statementRol.close()
                    }
                }

                insertStmt.close()
                return filas > 0

            } catch (e: SQLException) {
                println("Error al registrar usuario: ${e.message}")
                e.printStackTrace()
            } finally {
                connection.close()
            }
        } else {
            println("Error: No se pudo establecer conexión con la base de datos.")
        }

        return false
    }




    fun obtenerPorId(id: Int): Usuario? {
        val query = "SELECT * FROM usuario WHERE id = ?"
        var usuario: Usuario? = null

        val connection = Conexion.getConnection()
        if (connection != null) {
            try {
                val statement = connection.prepareStatement(query)
                statement.setInt(1, id)
                val result = statement.executeQuery()

                if (result.next()) {
                    usuario = result.toUsuario()
                }

                result.close()
                statement.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                connection.close()
            }
        } else {
            println("Error: No se pudo establecer conexión con la base de datos.")
        }

        return usuario
    }

    fun buscarPorNombre(nombre: String): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val query = "SELECT * FROM usuario WHERE nombre LIKE ?"

        val connection = Conexion.getConnection()
        if (connection != null) {
            try {
                val statement = connection.prepareStatement(query)
                statement.setString(1, "%$nombre%")
                val result = statement.executeQuery()

                while (result.next()) {
                    lista.add(result.toUsuario())
                }

                result.close()
                statement.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                connection.close()
            }
        } else {
            println("Error: No se pudo establecer conexión con la base de datos.")
        }

        return lista
    }

    fun actualizar(id: Int, usuario: Usuario): Boolean {
        val query = """
            UPDATE usuario SET 
                nombre = ?, 
                password = ?, 
                experiencia = ?, 
                nivel = ?, 
                casa_id = ?
            WHERE id = ?
        """.trimIndent()

        val connection = Conexion.getConnection()
        if (connection != null) {
            try {
                val statement = connection.prepareStatement(query)
                statement.setString(1, usuario.nombre)
                statement.setString(2, usuario.password)
                statement.setInt(3, usuario.experiencia)
                statement.setInt(4, usuario.nivel)
                statement.setInt(5, usuario.casa_id)
                statement.setInt(6, id)

                val filas = statement.executeUpdate()
                statement.close()
                return filas > 0
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                connection.close()
            }
        } else {
            println("Error: No se pudo establecer conexión con la base de datos.")
        }

        return false
    }

    fun borrar(id: Int): Boolean {
        val query = "DELETE FROM usuario WHERE id = ?"

        val connection = Conexion.getConnection()
        if (connection != null) {
            try {
                val statement = connection.prepareStatement(query)
                statement.setInt(1, id)
                val filas = statement.executeUpdate()
                statement.close()
                return filas > 0
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                connection.close()
            }
        } else {
            println("Error: No se pudo establecer conexión con la base de datos.")
        }

        return false
    }

    fun login(nombre: String, password: String): UsuarioLogeado? {
        val queryUsuario = "SELECT * FROM usuario WHERE nombre = ? AND password = ?"
        val queryRoles = """
            SELECT r.nombre
            FROM rol r
            JOIN usuario_rol ur ON r.id = ur.rol_id
            WHERE ur.usuario_id = ?
            """.trimIndent()


        val connection = Conexion.getConnection()
        if (connection != null) {
            try {
                val statementUsuario = connection.prepareStatement(queryUsuario)
                statementUsuario.setString(1, nombre)
                statementUsuario.setString(2, password)
                val resultUsuario = statementUsuario.executeQuery()

                if (resultUsuario.next()) {
                    val usuario = resultUsuario.toUsuario()
                    resultUsuario.close()
                    statementUsuario.close()

                    val roles = mutableListOf<String>()
                    val statementRoles = connection.prepareStatement(queryRoles)
                    statementRoles.setInt(1, usuario.id!!)
                    val resultRoles = statementRoles.executeQuery()

                    while (resultRoles.next()) {
                        roles.add(resultRoles.getString("nombre"))
                    }

                    resultRoles.close()
                    statementRoles.close()

                    // Color según casa
                    val coloresCasa = mapOf(
                        1 to "#FFD700", // Gryffindor
                        2 to "#008000", // Slytherin
                        3 to "#0000FF", // Ravenclaw
                        4 to "#A52A2A"  // Hufflepuff
                    )
                    val color = coloresCasa[usuario.casa_id] ?: "#CCCCCC"

                    return UsuarioLogeado(usuario, roles, color)
                }

                resultUsuario.close()
                statementUsuario.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                connection.close()
            }
        }

        return null
    }

    fun getHouseOccupancy(): Map<Int, Int> {
        val query = """
        SELECT casa.id as id, COUNT(usuario.id) as numGente
        FROM casa
        LEFT JOIN usuario ON casa.id = usuario.casa_id
        GROUP BY casa.id
    """.trimIndent()

        val connection = Conexion.getConnection()
        val occupancy = mutableMapOf<Int, Int>()

        if (connection != null) {
            try {
                val statement = connection.prepareStatement(query)
                val result = statement.executeQuery()
                while (result.next()) {
                    occupancy[result.getInt("id")] = result.getInt("numGente")
                }
                result.close()
                statement.close()
            } finally {
                connection.close()
            }
        }
        return occupancy
    }




    private fun ResultSet.toUsuario(): Usuario = Usuario(
        id = getInt("id"),
        nombre = getString("nombre"),
        password = getString("password"),
        experiencia = getInt("experiencia"),
        nivel = getInt("nivel"),
        casa_id = getInt("casa_id")
    )
}

package DAO

import Database.Conexion
import Model.Usuario
import Model.UsuarioLogeado
import java.sql.ResultSet
import java.sql.SQLException

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
        INSERT INTO usuario (nombre, password, experiencia, nivel, fk_casa_jad)
        VALUES (?, ?, ?, ?, ?)
    """.trimIndent()

        val connection = Conexion.getConnection()
        if (connection != null) {
            try {
                // Verificar si el nombre ya existe
                val checkStmt = connection.prepareStatement(existeQuery)
                checkStmt.setString(1, usuario.nombre)
                val result = checkStmt.executeQuery()
                result.next()
                val yaExiste = result.getInt(1) > 0
                result.close()
                checkStmt.close()

                if (yaExiste) {
                    println("❌ El usuario '${usuario.nombre}' ya está registrado.")
                    return false
                }

                // Insertar nuevo usuario
                val insertStmt = connection.prepareStatement(insertQuery)
                insertStmt.setString(1, usuario.nombre)
                insertStmt.setString(2, usuario.password)
                insertStmt.setInt(3, usuario.experiencia)
                insertStmt.setInt(4, usuario.nivel)
                insertStmt.setInt(5, usuario.casa_id)

                val filas = insertStmt.executeUpdate()
                insertStmt.close()
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
                fk_casa_jad = ?
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
        val queryRoles = "SELECT rol_id FROM usuario_rol WHERE usuario_id = ?"

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

                    // Obtener roles
                    val roles = mutableListOf<String>()
                    val statementRoles = connection.prepareStatement(queryRoles)
                    statementRoles.setInt(1, usuario.id!!)
                    val resultRoles = statementRoles.executeQuery()

                    while (resultRoles.next()) {
                        roles.add(resultRoles.getString("rol"))
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


    private fun ResultSet.toUsuario(): Usuario = Usuario(
        id = getInt("id"),
        nombre = getString("nombre"),
        password = getString("password"),
        experiencia = getInt("experiencia"),
        nivel = getInt("nivel"),
        casa_id = getInt("casa_id")
    )
}

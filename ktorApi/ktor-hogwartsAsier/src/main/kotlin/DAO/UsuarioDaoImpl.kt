package DAO

import Database.Conexion
import Model.Usuario
import Model.UsuarioLogeado
import com.example.Model.UsuarioCrear
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

object UsuarioDaoImpl {

    fun obtenerTodos(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val query = "SELECT * FROM usuario WHERE id != 2"

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
                        1 to "#A52A2A", // Gryffindor
                        2 to "#008000", // Slytherin
                        3 to "#0000FF", // Ravenclaw
                        4 to "#FFD700"  // Hufflepuff
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

    fun listadoProfesores(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val query = """
            SELECT u.* FROM usuario u
            JOIN usuario_rol ur ON u.id = ur.usuario_id
            JOIN rol r ON ur.rol_id = r.id
            WHERE r.nombre = 'profesor'
            """.trimIndent()

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

    fun asignarProfesorAAsignatura(asignaturaId: Int, profesorId: Int): Boolean {
        val query = "REPLACE INTO profesor_asignatura (asignatura_id, profesor_id) VALUES (?, ?)"

        val connection = Conexion.getConnection() ?: return false

        return try {
            val statement = connection.prepareStatement(query)
            statement.setInt(1, asignaturaId)
            statement.setInt(2, profesorId)

            val affectedRows = statement.executeUpdate()
            statement.close()
            affectedRows > 0
        } catch (e: SQLException) {
            println("Error en BD al asignar profesor: ${e.message}")
            false
        } finally {
            connection.close()
        }
    }

    fun getTodosLosRoles(): List<String> {
        val query = "SELECT nombre FROM rol"
        val connection = Conexion.getConnection() ?: return emptyList()
        val roles = mutableListOf<String>()

        return try {
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                roles.add(resultSet.getString("nombre"))
            }
            statement.close()
            roles
        } catch (e: SQLException) {
            println("Error en BD al obtener roles: ${e.message}")
            emptyList()
        } finally {
            connection.close()
        }
    }

    fun cambiarRolDeUsuario(usuarioId: Int, nombreNuevoRol: String): Boolean {
        val connection = Conexion.getConnection() ?: return false

        return try {
            val rolIdQuery = "SELECT id FROM rol WHERE nombre = ?"
            val rolIdStatement = connection.prepareStatement(rolIdQuery)
            rolIdStatement.setString(1, nombreNuevoRol)
            val rolIdResult = rolIdStatement.executeQuery()

            if (!rolIdResult.next()) {
                println("El rol '$nombreNuevoRol' no existe.")
                rolIdStatement.close()
                return false
            }
            val idDelNuevoRol = rolIdResult.getInt("id")
            rolIdStatement.close()

            val updateQuery = "REPLACE INTO usuario_rol (usuario_id, rol_id) VALUES (?, ?)"
            val updateStatement = connection.prepareStatement(updateQuery)
            updateStatement.setInt(1, usuarioId)
            updateStatement.setInt(2, idDelNuevoRol)

            val affectedRows = updateStatement.executeUpdate()
            updateStatement.close()

            affectedRows > 0
        } catch (e: SQLException) {
            println("Error en BD al cambiar rol: ${e.message}")
            false
        } finally {
            connection.close()
        }
    }

    fun crearUsuario(datosUsuario: UsuarioCrear): Boolean {
        val connection = Conexion.getConnection() ?: return false
        connection.autoCommit = false

        try {
            val userQuery = "INSERT INTO usuario (nombre, password, casa_id, nivel, experiencia) VALUES (?, ?, ?, ?, ?)"
            val userStatement = connection.prepareStatement(userQuery, java.sql.Statement.RETURN_GENERATED_KEYS)
            userStatement.setString(1, datosUsuario.nombre)
            userStatement.setString(2, datosUsuario.password)
            userStatement.setInt(3, datosUsuario.casaId)
            userStatement.setInt(4, datosUsuario.nivel)
            userStatement.setInt(5, datosUsuario.experiencia)
            userStatement.executeUpdate()

            val generatedKeys = userStatement.generatedKeys
            if (!generatedKeys.next()) {
                println("ERROR: No se pudo obtener el ID del nuevo usuario.")
                connection.rollback()
                return false
            }
            val nuevoUsuarioId = generatedKeys.getInt(1)
            userStatement.close()

            val rolIdQuery = "SELECT id FROM rol WHERE nombre = ?"
            val rolIdStatement = connection.prepareStatement(rolIdQuery)
            rolIdStatement.setString(1, datosUsuario.rol)
            val rolIdResult = rolIdStatement.executeQuery()

            if (!rolIdResult.next()) {
                println("ERROR: El rol '${datosUsuario.rol}' no existe.")
                connection.rollback()
                return false
            }
            val rolId = rolIdResult.getInt("id")
            rolIdStatement.close()

            val rolQuery = "INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (?, ?)"
            val rolStatement = connection.prepareStatement(rolQuery)
            rolStatement.setInt(1, nuevoUsuarioId)
            rolStatement.setInt(2, rolId)
            val affectedRows = rolStatement.executeUpdate()
            rolStatement.close()

            connection.commit()
            return affectedRows > 0

        } catch (e: SQLException) {
            println("Error en BD al crear usuario: ${e.message}")
            e.printStackTrace()
            connection.rollback()
            return false
        } finally {
            connection.autoCommit = true
            connection.close()
        }
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

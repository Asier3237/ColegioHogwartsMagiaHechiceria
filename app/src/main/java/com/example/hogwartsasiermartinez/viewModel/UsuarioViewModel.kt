package com.example.hogwartsasiermartinez.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hogwartsasiermartinez.Api.UserNetwork
import com.example.hogwartsasiermartinez.model.Usuario
import androidx.lifecycle.viewModelScope
import com.example.Model.UsuarioCrear
import com.example.hogwartsasiermartinez.model.UsuarioLogeado
import com.example.hogwartsasiermartinez.model.UsuarioLogin
import kotlinx.coroutines.launch


class UsuarioViewModel : ViewModel() {

    var nombreAux: String? = null
    var passwdAux: String? = null

    private val _usuarios = MutableLiveData<List<Usuario>>()
    val usuarios: LiveData<List<Usuario>> get() = _usuarios

    private val _usuarioSeleccionado = MutableLiveData<UsuarioLogeado?>()
    val usuarioSeleccionado: LiveData<UsuarioLogeado?> get() = _usuarioSeleccionado

    private val _usuarioSeleccionadoId = MutableLiveData<Usuario?>()
    val usuarioSeleccionadoId: LiveData<Usuario?> get() = _usuarioSeleccionadoId

    private val _operacionExitosa = MutableLiveData<Boolean>()
    val operacionExitosa: LiveData<Boolean> get() = _operacionExitosa

    private val _casaSeleccionadaId = MutableLiveData<Int?>()
    val casaSeleccionadaId: LiveData<Int?> get() = _casaSeleccionadaId

    private val _roles = MutableLiveData<List<String>>()
    val roles: LiveData<List<String>> get() = _roles

    private val _actualizacionExitosa = MutableLiveData<Boolean?>()
    val actualizacionExitosa: LiveData<Boolean?> get() = _actualizacionExitosa

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _creacionExitosa = MutableLiveData<Boolean?>()
    val creacionExitosa: LiveData<Boolean?> get() = _creacionExitosa

    // lista los roles y los usuarios nada más inicializar el viewModel
    init {
        getRoles()
        getUsers()
    }

    // pide a la api que elija una casa según las preferencias
    fun selectHouse(preferences: List<Int>) {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.selectHouse(preferences)
                val casaId = response.body()
                Log.d("Registro", "Respuesta selectHouse: ${casaId}")

                if (response.isSuccessful && casaId != null && casaId > 0) {
                    _casaSeleccionadaId.value = casaId
                } else {
                    _casaSeleccionadaId.value = -1
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _casaSeleccionadaId.value = -1
            }
        }
    }

    // pide la lista completa de usuarios a la api
    fun getUsers() {
        viewModelScope.launch {
            try {
                val resultado = UserNetwork.retrofit.getUsuarios()
                if (resultado.isSuccessful) {
                    _usuarios.value = resultado.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // pide los datos de un solo usuario por su id
    fun getUserById(id: Int){
        viewModelScope.launch {
            try {
                val resultado = UserNetwork.retrofit.getUsuario(id)
                if (resultado.isSuccessful) {
                    _usuarioSeleccionadoId.value = resultado.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // manda el nombre y la contraseña a la api para hacer login
    fun login(nombre: String, pwd: String) {
        viewModelScope.launch {
            try {
                val datos = UsuarioLogin(nombre, pwd)
                val response = UserNetwork.retrofit.login(datos)
                if (response.isSuccessful) {
                    _usuarioSeleccionado.value = response.body()
                } else {
                    _usuarioSeleccionado.value = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _usuarioSeleccionado.value = null
            }
        }
    }

    // añade un usuario nuevo desde la pantalla de registro
    fun addUser(usuario: Usuario) {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.addUsuario(usuario)
                if (!response.isSuccessful) {
                    Log.e("Registro", "Error al insertar usuario: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // actualiza los datos de un usuario
    fun updateUser(id: Int, usuario: Usuario){
        viewModelScope.launch {
            try {
                val resultado = UserNetwork.retrofit.updateUsuario(id, usuario)
                if (resultado.isSuccessful){
                    _operacionExitosa.value = resultado.body()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    // borra un usuario por su id
    fun deleteUser(id: Int?){
        viewModelScope.launch {
            try {
                val resultado = UserNetwork.retrofit.deleteUsuario(id)
                if (resultado.isSuccessful){
                    _operacionExitosa.value = resultado.body()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    // pide la lista de todos los roles a la api
    private fun getRoles() {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.getRoles()
                if (response.isSuccessful) {
                    _roles.postValue(response.body())
                } else {
                    _error.postValue("Error al cargar roles: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error de red: No se pudieron cargar los roles.")
            }
        }
    }

    // le dice a la api que cambie el rol de un usuario
    fun cambiarRol(idUsuario: Int?, nuevoRol: String) {
        if (idUsuario == null) {
            _error.postValue("Error: El ID del usuario es nulo.")
            return
        }

        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.cambiarRol(idUsuario, nuevoRol)
                if (response.isSuccessful) {
                    _actualizacionExitosa.postValue(true)
                    // recargamos la lista para que se vea el cambio
                    getUsers()
                } else {
                    _error.postValue("Fallo al cambiar rol: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error de red: No se pudo cambiar el rol.")
            }
        }
    }

    // limpia el livedata de la actualización para que el mensaje no se repita
    fun onActualizacionCompletada() {
        _actualizacionExitosa.value = null
        _error.value = null
    }

    // crea un usuario nuevo desde la pantalla del admin
    fun adminCrearUsuario(datosUsuario: UsuarioCrear) {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.adminCrearUsuario(datosUsuario)
                if (response.isSuccessful) {
                    _creacionExitosa.postValue(true)
                } else {
                    _error.postValue("Error al crear usuario: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error de red: ${e.message}")
            }
        }
    }

    // limpia el livedata de la creación
    fun onCreacionCompletada() {
        _creacionExitosa.value = null
    }

}
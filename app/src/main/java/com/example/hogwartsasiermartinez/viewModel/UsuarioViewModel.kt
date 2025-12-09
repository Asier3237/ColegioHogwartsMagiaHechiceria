package com.example.hogwartsasiermartinez.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hogwartsasiermartinez.Api.UserNetwork
import com.example.hogwartsasiermartinez.model.Usuario
import androidx.lifecycle.viewModelScope
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

}
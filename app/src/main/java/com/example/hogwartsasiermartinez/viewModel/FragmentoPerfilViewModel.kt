package com.example.hogwartsasiermartinez.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hogwartsasiermartinez.Api.UserNetwork
import com.example.hogwartsasiermartinez.model.Usuario
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {
    private val _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> = _usuario

    fun cargarPerfil(usuarioId: Int) {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.getUsuario(usuarioId)
                if (response.isSuccessful) {
                    _usuario.value = response.body()
                }
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }
}


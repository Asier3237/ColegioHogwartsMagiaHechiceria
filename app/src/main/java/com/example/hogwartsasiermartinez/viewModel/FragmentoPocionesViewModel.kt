package com.example.hogwartsasiermartinez.viewModel

import androidx.lifecycle.*
import com.example.Model.Ingrediente
import com.example.Model.IngredientePocima
import com.example.Model.Pocima
import com.example.Model.PocimaCrear
import com.example.hogwartsasiermartinez.Api.UserNetwork
import com.example.hogwartsasiermartinez.model.*
import kotlinx.coroutines.launch

class FragmentoPocionesViewModel : ViewModel() {

    // --- LiveData para la UI ---
    private val _pociones = MutableLiveData<List<Pocima>>()
    val pociones: LiveData<List<Pocima>> get() = _pociones

    private val _ingredientes = MutableLiveData<List<Ingrediente>>()
    val ingredientes: LiveData<List<Ingrediente>> get() = _ingredientes

    private val _operacionExitosa = MutableLiveData<String?>()
    val operacionExitosa: LiveData<String?> get() = _operacionExitosa

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        // Cargar los ingredientes una sola vez al inicio
        cargarIngredientes()
    }

    // --- Funciones para interactuar con la API ---

    fun cargarPociones(rol: String, usuarioId: Int) {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.getPociones(rol, usuarioId)
                if (response.isSuccessful) {
                    _pociones.postValue(response.body())
                } else {
                    _error.postValue("Error al cargar pociones: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error de red: ${e.message}")
            }
        }
    }

    private fun cargarIngredientes() {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.getIngredientes()
                if (response.isSuccessful) {
                    _ingredientes.postValue(response.body())
                }
            } catch (e: Exception) {
                _error.postValue("Error al cargar ingredientes.")
            }
        }
    }

    fun crearPocion(nombre: String, resumen: String, creadorId: Int, ingredientes: List<IngredientePocima>) {
        viewModelScope.launch {
            val pocimaData = PocimaCrear(nombre, resumen, creadorId, ingredientes)
            try {
                val response = UserNetwork.retrofit.crearPocion(pocimaData)
                if (response.isSuccessful) {
                    _operacionExitosa.postValue("¡Poción enviada para validación! Has ganado 2 EXP.")
                } else {
                    _error.postValue("Fallo al crear la poción: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error de red: ${e.message}")
            }
        }
    }

    fun validarPocion(pocionId: Int, esValidada: Boolean) {
        val nuevoEstado = if (esValidada) 1 else 2 // 1 = validada, 2 = rechazada
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.validarPocion(pocionId, nuevoEstado)
                if (response.isSuccessful) {
                    _operacionExitosa.postValue("Poción ${if (esValidada) "validada" else "rechazada"}.")
                } else {
                    _error.postValue("Error al actualizar: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error de red: ${e.message}")
            }
        }
    }

    fun borrarPocion(pocionId: Int) {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.borrarPocion(pocionId)
                if (response.isSuccessful) {
                    _operacionExitosa.postValue("Poción eliminada correctamente.")
                } else {
                    _error.postValue("Error al eliminar: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error de red: ${e.message}")
            }
        }
    }

    // Función para limpiar los mensajes y evitar que se muestren de nuevo
    fun onOperacionCompletada() {
        _operacionExitosa.value = null
        _error.value = null
    }
}

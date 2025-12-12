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

    private val _pociones = MutableLiveData<List<Pocima>>()
    val pociones: LiveData<List<Pocima>> get() = _pociones

    private val _ingredientes = MutableLiveData<List<Ingrediente>>()
    val ingredientes: LiveData<List<Ingrediente>> get() = _ingredientes

    //livedata para verificar que ha ido bien
    private val _operacionExitosa = MutableLiveData<String?>()
    val operacionExitosa: LiveData<String?> get() = _operacionExitosa

    //livedata para mostrar errores
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    //la lista de ingredientes se carga nada más inicializar el fragmento
    init {
        cargarIngredientes()
    }

    //carga la lista de pociones de la bd, dependiendo del rol del usuario
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

    //carga los ingredientes de la bd
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

    //permite crear una poción a los alumnos
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

    //permite tanto a profesores ocmo a los admin validar las pociones pendientes
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

    //permite borrar pociones de la bd
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

    // simplemente daje las dos livedata vacías para que no haya fallos en los mensajes
    fun onOperacionCompletada() {
        _operacionExitosa.value = null
        _error.value = null
    }
}

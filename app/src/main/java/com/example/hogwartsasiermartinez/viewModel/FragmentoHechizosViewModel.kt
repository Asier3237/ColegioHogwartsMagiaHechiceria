package com.example.hogwartsasiermartinez.viewModel

import androidx.lifecycle.*
import com.example.hogwartsasiermartinez.Api.UserNetwork
import com.example.hogwartsasiermartinez.model.* // Cambiado a 'model'
import kotlinx.coroutines.launch

// Nombre de la clase corregido para seguir la convención
class FragmentoHechizosViewModel : ViewModel() {

    private val _hechizos = MutableLiveData<List<Hechizo>>()
    val hechizos: LiveData<List<Hechizo>> get() = _hechizos

    private val _operacionExitosa = MutableLiveData<String?>()
    val operacionExitosa: LiveData<String?> get() = _operacionExitosa

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        cargarHechizos()
    }

    fun cargarHechizos() {
        viewModelScope.launch {
            try {
                // He corregido la ruta para que coincida con tu UserApi.kt
                val response = UserNetwork.retrofit.getHechizos()
                if (response.isSuccessful) {
                    _hechizos.postValue(response.body())
                } else {
                    _error.postValue("Error al cargar hechizos: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error de red: ${e.message}")
            }
        }
    }

    fun crearHechizo(nombre: String, descripcion: String, experiencia: Int) {
        viewModelScope.launch {
            try {
                // El ID es 0 porque lo autogenera la BD
                val nuevoHechizo = Hechizo(id = 0, nombre = nombre, descripcion = descripcion, experiencia = experiencia)
                // He corregido la ruta para que coincida con tu UserApi.kt
                val response = UserNetwork.retrofit.crearHechizo(nuevoHechizo)
                if (response.isSuccessful) {
                    _operacionExitosa.postValue("¡Hechizo creado con éxito!")
                    cargarHechizos() // Recargamos la lista
                } else {
                    _error.postValue("Fallo al crear hechizo: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error de red: ${e.message}")
            }
        }
    }

    fun alumnoAprendeHechizo(alumnoId: Int, hechizoId: Int) {
        viewModelScope.launch {
            // He cambiado el nombre de la data class para que coincida con tu UserApi.kt (HechizoUsu)
            val request = HechizoUsu(alumnoId, hechizoId)
            try {
                // He corregido la ruta para que coincida con tu UserApi.kt
                val response = UserNetwork.retrofit.aprenderHechizo(request)
                if (response.isSuccessful) {
                    _operacionExitosa.postValue("¡Hechizo aprendido!")
                } else {
                    _error.postValue("Fallo al aprender hechizo: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error de red: ${e.message}")
            }
        }
    }

    fun borrarHechizo(hechizoId: Int) {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.borrarHechizo(hechizoId)
                if (response.isSuccessful) {
                    _operacionExitosa.postValue("Hechizo eliminado.")
                    // Recargamos la lista para que el hechizo borrado desaparezca de la UI
                    cargarHechizos()
                } else {
                    _error.postValue("Fallo al borrar el hechizo: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error de red: ${e.message}")
            }
        }
    }

    fun onOperacionCompletada() {
        _operacionExitosa.value = null
        _error.value = null
    }
}

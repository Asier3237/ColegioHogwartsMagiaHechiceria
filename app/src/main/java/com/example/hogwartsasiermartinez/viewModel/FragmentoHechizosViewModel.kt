package com.example.hogwartsasiermartinez.viewModel

import androidx.lifecycle.*
import com.example.hogwartsasiermartinez.Api.UserNetwork
import com.example.hogwartsasiermartinez.model.*
import kotlinx.coroutines.launch

class FragmentoHechizosViewModel : ViewModel() {

    private val _hechizos = MutableLiveData<List<Hechizo>>()
    val hechizos: LiveData<List<Hechizo>> get() = _hechizos

    //livedata para verii¡ficar que todo fue bien
    private val _operacionExitosa = MutableLiveData<String?>()
    val operacionExitosa: LiveData<String?> get() = _operacionExitosa

    // livedata para errores
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // se carga la lista de hechizos nada más inicializar el viewModel
    init {
        cargarHechizos()
    }

    //se cargan todos los hechizops desde la api
    fun cargarHechizos() {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.getHechizos()
                if (response.isSuccessful) {
                    // si todo va bien, actualizamos la 'caja' con la nueva lista
                    _hechizos.postValue(response.body())
                } else {
                    _error.postValue("Error al cargar hechizos: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error de red: ${e.message}")
            }
        }
    }

    ///se crea un hechizo en la bd comunicandose con la api
    fun crearHechizo(nombre: String, descripcion: String, experiencia: Int) {
        viewModelScope.launch {
            try {
                // se crea el objeto para mandarlo a la api
                val nuevoHechizo = Hechizo(id = 0, nombre = nombre, descripcion = descripcion, experiencia = experiencia)
                val response = UserNetwork.retrofit.crearHechizo(nuevoHechizo)
                if (response.isSuccessful) {
                    // si se crea bien, recargamos la lista
                    _operacionExitosa.postValue("¡Hechizo creado con éxito!")
                    cargarHechizos()
                } else {
                    _error.postValue("Fallo al crear hechizo: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error de red: ${e.message}")
            }
        }
    }

    // un alumno puede aprender un hechizo, lo que se reffleja en la bd, mediante la api
    fun alumnoAprendeHechizo(alumnoId: Int, hechizoId: Int) {
        viewModelScope.launch {
            val request = HechizoUsu(alumnoId, hechizoId)
            try {
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

    //se puede boorrar un hechizo, que se reflejaría en la bd mediante la api
    fun borrarHechizo(hechizoId: Int) {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.borrarHechizo(hechizoId)
                if (response.isSuccessful) {
                    // si se borra bien recargamos la lista para que desaparezca
                    _operacionExitosa.postValue("Hechizo eliminado.")
                    cargarHechizos()
                } else {
                    _error.postValue("Fallo al borrar el hechizo: ${response.code()}")
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
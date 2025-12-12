package com.example.hogwartsasiermartinez.viewModel

import android.util.Log
import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hogwartsasiermartinez.Api.UserNetwork
// Importa el modelo Asignatura y el modelo Usuario
import com.example.hogwartsasiermartinez.model.Asignatura
import com.example.hogwartsasiermartinez.model.Usuario
import kotlinx.coroutines.launch

class FragmentoAsignaturasViewModel : ViewModel() {

    // LiveData para la lista de asignaturas
    private val _asignaturasLiveData = MutableLiveData<List<Asignatura>>()
    val asignaturasLiveData: LiveData<List<Asignatura>> get() = _asignaturasLiveData

    // LiveData para la lista de profesores
    private val _profesoresLiveData = MutableLiveData<List<Usuario>>()
    val profesoresLiveData: LiveData<List<Usuario>> get() = _profesoresLiveData

    // LiveData para los errores
    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    // LiveData para verificar que se hizoo bien
    private val _asignacionExitosa = MutableLiveData<Boolean>()
    val asignacionExitosa: LiveData<Boolean> get() = _asignacionExitosa

    //carga todas las asignaturas desde la api
    fun cargarAsignaturas() {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.getAsignaturas()
                if (response.isSuccessful && response.body() != null) {
                    _asignaturasLiveData.postValue(response.body())
                } else {
                    _errorLiveData.postValue("Error al cargar asignaturas: ${response.code()}")
                    Log.e("AsignaturasVM", "Error al cargar asignaturas: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Excepción al cargar asignaturas: ${e.message}") // se le asigna valor por el posible error
                Log.e("AsignaturasVM", "Excepción: ${e.message}")
            }
        }
    }

    //carga lista de profesores desde la api
    fun cargarProfesores() {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.getProfesores()
                if (response.isSuccessful && response.body() != null) {
                    _profesoresLiveData.postValue(response.body())
                    Log.d("ViewModel", "Profesores cargados: ${response.body()?.size ?: 0}")
                } else {
                    _errorLiveData.postValue("Error al cargar profesores: ${response.code()}")
                    Log.e("ViewModel", "Error cargando profesores: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Error al cargar profesores: ${e.message}")
                Log.e("ViewModel", "Excepción cargando profesores: ${e.message}")
            }
        }
    }

    // Realiza la asignación de profesores en la bd, comunicandose con la api
    fun asignarProfesorAAsignatura(idAsignatura: Int, idProfesor: Int) {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.asignarProfesor(idAsignatura, idProfesor)

                if (response.isSuccessful) {
                    Log.d("ViewModel", "Profesor $idProfesor asignado a asignatura $idAsignatura con éxito.")
                    _asignacionExitosa.postValue(true)
                } else {
                    _errorLiveData.postValue("Fallo al asignar: ${response.code()}")
                    Log.e("ViewModel", "Fallo en la API al asignar profesor: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Error al asignar el profesor: ${e.message}")
                Log.e("ViewModel", "Excepción al asignar profesor: ${e.message}")
            }
        }
    }

    fun onAsignacionCompletada() {
        _asignacionExitosa.value = false
    }
}

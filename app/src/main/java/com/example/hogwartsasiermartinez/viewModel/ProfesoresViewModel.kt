package com.example.hogwartsasiermartinez.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hogwartsasiermartinez.Api.UserNetwork
import com.example.hogwartsasiermartinez.model.Usuario
import kotlinx.coroutines.launch

class ProfesoresViewModel : ViewModel() {

    private val _profesoresLiveData = MutableLiveData<List<Usuario>>()
    val profesoresLiveData: LiveData<List<Usuario>> get() = _profesoresLiveData

    //carga una lista con todos los profesores
    fun cargarProfesores() {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.getProfesores()
                if (response.isSuccessful) {
                    response.body()?.let { lista ->
                        _profesoresLiveData.value = lista
                    }
                } else {
                    Log.e("ProfesoresVM", "Error en la respuesta: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ProfesoresVM", "Error cargando profesores: ${e.message}")
            }
        }
    }

    //permite asignar un profesor a una asignatura
    fun asignarProfesor(profesorId: Int, asignaturaId: Int) {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.asignarProfesor(asignaturaId, profesorId)
                if (response.isSuccessful) {
                    Log.d("ProfesoresVM", "Profesor asignado correctamente")
                } else {
                    Log.e("ProfesoresVM", "Error asignando profesor: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ProfesoresVM", "Error asignando profesor: ${e.message}")
            }
        }
    }
}


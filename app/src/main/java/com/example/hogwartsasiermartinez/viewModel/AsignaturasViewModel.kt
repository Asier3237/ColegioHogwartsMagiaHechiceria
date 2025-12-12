package com.example.hogwartsasiermartinez.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hogwartsasiermartinez.Api.UserNetwork
import com.example.hogwartsasiermartinez.model.Asignatura
import kotlinx.coroutines.launch

class AsignaturasViewModel : ViewModel() {

    private val _asignaturasLiveData = MutableLiveData<List<Asignatura>>()
    val asignaturasLiveData: LiveData<List<Asignatura>> get() = _asignaturasLiveData

    // carga la lista de asignaturas desde la api
    fun cargarAsignaturas() {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.getAsignaturas()

                // se comprueba si no ha dado fallo o si no está vacía
                if (response.isSuccessful && response.body() != null) {
                    // se actualiza la variable privada con la lista devuelta desde la api
                    _asignaturasLiveData.value = response.body()
                } else {
                    Log.e("AsignaturasViewModel", "Error en la respuesta: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AsignaturasViewModel", "Excepción al cargar asignaturas: ${e.message}")
            }
        }
    }
}
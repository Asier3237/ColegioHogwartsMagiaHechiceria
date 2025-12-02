package com.example.hogwartsasiermartinez.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hogwartsasiermartinez.Api.UserNetwork
import com.example.hogwartsasiermartinez.model.Casa
import kotlinx.coroutines.launch
import android.util.Log

class CasasViewModel : ViewModel() {

    private val _casasLiveData = MutableLiveData<List<Casa>>()
    val casasLiveData: LiveData<List<Casa>> get() = _casasLiveData

    fun cargarCasas() {
        viewModelScope.launch {
            try {
                Log.d("CasasViewModel", "Llamando a getCasas()")
                val response = UserNetwork.retrofit.getCasas()
                Log.d("CasasViewModel", "Código: ${response.code()}, Body: ${response.body()}")

                if (response.isSuccessful && response.body() != null) {
                    _casasLiveData.value = response.body()
                } else {
                    Log.e("CasasViewModel", "Error en la respuesta: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CasasViewModel", "Excepción al cargar casas: ${e.message}")
                e.printStackTrace()
            }
        }
    }


}

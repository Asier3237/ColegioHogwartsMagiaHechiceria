package com.example.hogwartsasiermartinez.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hogwartsasiermartinez.Api.UserNetwork
import com.example.hogwartsasiermartinez.model.Casa
import kotlinx.coroutines.launch

class CasasViewModel : ViewModel() {

    private val _casasLiveData = MutableLiveData<List<Casa>>()
    val casasLiveData: LiveData<List<Casa>> get() = _casasLiveData

    private val _rankingLiveData = MutableLiveData<List<Casa>>()
    val rankingLiveData: LiveData<List<Casa>> get() = _rankingLiveData

    // carga todas las casas desde la api
    fun cargarCasas() {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.getCasas()
                if (response.isSuccessful) {
                    _casasLiveData.value = response.body()
                } else {
                    Log.e("CasasViewModel", "Error en la respuesta: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CasasViewModel", "Excepción al cargar casas: ${e.message}")
            }
        }
    }

    // carga la lista de casas pero ordenada para el ranking
    fun cargarRanking() {
        viewModelScope.launch {
            try {
                val response = UserNetwork.retrofit.getRankingCasas()
                if (response.isSuccessful) {
                    _rankingLiveData.value = response.body()
                } else {
                    Log.e("CasasViewModel", "Error en la respuesta del ranking: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CasasViewModel", "Excepción al cargar ranking: ${e.message}")
            }
        }
    }
}
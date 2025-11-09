package com.example.hogwartsasiermartinez.Api

import com.example.hogwartsasiermartinez.Parametros.Parametros
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserNetwork {
    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("${Parametros.url}:${Parametros.puerto}/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserAPI::class.java)
    }
}

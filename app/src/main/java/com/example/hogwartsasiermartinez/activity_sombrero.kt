package com.example.hogwartsasiermartinez

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hogwartsasiermartinez.Auxiliar.Sesion
import com.example.hogwartsasiermartinez.databinding.ActivityMainBinding
import com.example.hogwartsasiermartinez.databinding.ActivityRegistroBinding
import com.example.hogwartsasiermartinez.databinding.ActivitySombreroBinding
import com.example.hogwartsasiermartinez.viewModel.UsuarioViewModel
import kotlin.getValue

class activity_sombrero : AppCompatActivity() {

    lateinit var binding: ActivitySombreroBinding
    private val viewModel : UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySombreroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.imgSombrero.setImageResource(R.drawable.sombreroseleccionador)

        val casaId = Sesion.casaId
        Log.d("Registro", "CasaId = $casaId")

        val (nombreCasa, imagenResId) = when (casaId) {
            1 -> "Gryffindor" to R.drawable.gryffindor
            2 -> "Slytherin" to R.drawable.slytherin
            3 -> "Ravenclaw" to R.drawable.ravenclaw
            4 -> "Hufflepuff" to R.drawable.hufflepuff
            else -> "Sin casa" to R.drawable.sombreroseleccionador
        }

        binding.tvPickCasa.text = "Has sido asignado a $nombreCasa"
        binding.imgCasa.setImageResource(imagenResId)

        binding.btVueltaLogin.setOnClickListener {
            var intentVMain = Intent(this, MainActivity::class.java)
            startActivity(intentVMain)
        }

    }
}
package com.example.hogwartsasiermartinez

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        setContentView(R.layout.activity_sombrero)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btVueltaLogin.setOnClickListener {
            finish()
        }

    }
}
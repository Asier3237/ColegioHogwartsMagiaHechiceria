package com.example.hogwartsasiermartinez

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hogwartsasiermartinez.databinding.ActivityMainBinding
import com.example.hogwartsasiermartinez.databinding.ActivityRegistroBinding
import com.example.hogwartsasiermartinez.model.Usuario
import com.example.hogwartsasiermartinez.viewModel.UsuarioViewModel
import kotlin.getValue

class activity_registro : AppCompatActivity() {

    lateinit var binding: ActivityRegistroBinding
    private val viewModel : UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btRegistro.setOnClickListener {
            var nom = binding.etNombreReg.text.toString()
            var passwd = binding.etPasswdReg.text.toString()
            var usu = Usuario(null, nom, passwd, null, null, )

            viewModel.addUser()
        }

        binding.tvVuelta.setOnClickListener {
            finish()
        }
    }
}
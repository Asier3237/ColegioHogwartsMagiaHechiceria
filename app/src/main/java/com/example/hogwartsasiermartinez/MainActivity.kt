package com.example.hogwartsasiermartinez

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hogwartsasiermartinez.Auxiliar.Sesion
import com.example.hogwartsasiermartinez.databinding.ActivityMainBinding
import com.example.hogwartsasiermartinez.viewModel.UsuarioViewModel

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val viewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btInicioSesion.setOnClickListener {
            val nombre = binding.etNom.text.toString()
            val passwd = binding.etPasswd.text.toString()

            if (nombre.isNotEmpty() && passwd.isNotEmpty()) {
                viewModel.login(nombre, passwd)
            } else {
                Toast.makeText(this, "No puedes dejar campos vacíos", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.usuarioSeleccionado.observe(this) { usuarioLogeado ->
            if (usuarioLogeado != null) {
                if (usuarioLogeado.roles.size > 1) {
                    val intent = Intent(this, SeleccionRolActivity::class.java)
                    Sesion.roles = usuarioLogeado.roles

                    Sesion.usuarioId = usuarioLogeado.usuario.id ?: -1
                    Sesion.colorCasa = usuarioLogeado.colorCasa

                    startActivity(intent)
                } else if (usuarioLogeado.roles.isNotEmpty()) {
                    val rol = usuarioLogeado.roles.first()
                    val usuarioId = usuarioLogeado.usuario.id
                    if (usuarioId != null) {
                        Sesion.usuarioId = usuarioId
                        Sesion.rolActivo = rol
                        Sesion.colorCasa = usuarioLogeado.colorCasa
                        navegarSegunRol(rol)
                    } else {
                        Toast.makeText(this, "ID de usuario no disponible", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "El usuario no tiene roles asignados", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegistro.setOnClickListener {
            val intentVAdmin = Intent(this, activity_registro::class.java)
            startActivity(intentVAdmin)
        }
    }

    private fun navegarSegunRol(rol: String) {
        when (rol.lowercase()) {
            "alumno" -> startActivity(Intent(this, AlumnosActivity::class.java))
            "profesor" -> startActivity(Intent(this, ProfesorActivity::class.java))
            "admin" -> startActivity(Intent(this, AdminActivity::class.java))
        }
    }
}

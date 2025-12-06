package com.example.hogwartsasiermartinez

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hogwartsasiermartinez.databinding.ActivityMainBinding
import com.example.hogwartsasiermartinez.viewModel.UsuarioViewModel
import kotlin.getValue
import kotlin.jvm.java

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
            var nombre = binding.etNom.text.toString()
            var passwd = binding.etPasswd.text.toString()

            if (!nombre.isEmpty() && !passwd.isEmpty()) {
                viewModel.login(nombre, passwd)
            } else {
                Toast.makeText(this, "No puedes dejar campos vacíos", Toast.LENGTH_SHORT).show()
            }

        }

        viewModel.usuarioSeleccionado.observe(this) { usuarioLogeado ->
            if (usuarioLogeado != null) {
                if (usuarioLogeado.roles.size > 1) {
                    val intent = Intent(this, SeleccionRolActivity::class.java)
                    intent.putStringArrayListExtra("roles", ArrayList(usuarioLogeado.roles))
                    intent.putExtra("usuarioId", usuarioLogeado.usuario.id)
                    startActivity(intent)
                } else if (usuarioLogeado.roles.isNotEmpty()) {
                    val rol = usuarioLogeado.roles.first()
                    val usuarioId = usuarioLogeado.usuario.id
                    if (usuarioId != null) {
                        navegarSegunRol(rol, usuarioId)
                    } else {
                        Toast.makeText(this, "ID de usuario no disponible", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "El usuario no tiene roles asignados", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegistro.setOnClickListener {
            var intentVAdmin = Intent(this, activity_registro::class.java)
            startActivity(intentVAdmin)
        }
    }

    private fun navegarSegunRol(rol: String, usuarioId: Int) {
        when (rol.lowercase()) {
            "alumno" -> startActivity(Intent(this, AlumnosActivity::class.java).apply {
                putExtra("usuarioId", usuarioId)
            })

            "profesor" -> startActivity(Intent(this, ProfesorActivity::class.java).apply {
                putExtra("usuarioId", usuarioId)
            })

            "admin" -> startActivity(Intent(this, AdminActivity::class.java).apply {
                putExtra("usuarioId", usuarioId)
            })
        }
    }


}
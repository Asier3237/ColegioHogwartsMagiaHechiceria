package com.example.hogwartsasiermartinez

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hogwartsasiermartinez.Auxiliar.Sesion
import com.example.hogwartsasiermartinez.databinding.ActivitySeleccionRolBinding

class SeleccionRolActivity : AppCompatActivity() {

    lateinit var binding: ActivitySeleccionRolBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySeleccionRolBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val roles = Sesion.roles
        val usuarioId = Sesion.usuarioId

        if (roles.isEmpty() || usuarioId == -1) {
            Toast.makeText(this, "No se pudieron cargar los roles", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Selecciona tu rol")
            .setItems(roles.toTypedArray()) { _, which ->
                val rolSeleccionado = roles[which]
                Sesion.rolActivo = rolSeleccionado
                navegarSegunRol(rolSeleccionado, usuarioId)
                finish()
            }
            .setCancelable(false)
            .show()
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
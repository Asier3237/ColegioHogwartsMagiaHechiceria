package com.example.hogwartsasiermartinez

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hogwartsasiermartinez.databinding.ActivityAdminBinding
import com.example.hogwartsasiermartinez.databinding.ActivityAlumnosBinding
import com.example.hogwartsasiermartinez.databinding.ActivityProfesorBinding

class ProfesorActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfesorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfesorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usuarioId = intent.getIntExtra("usuarioId", -1)

    }
}
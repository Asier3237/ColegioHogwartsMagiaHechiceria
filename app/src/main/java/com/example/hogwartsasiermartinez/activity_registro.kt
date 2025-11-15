package com.example.hogwartsasiermartinez

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hogwartsasiermartinez.Adapters.CasasAdapter
import com.example.hogwartsasiermartinez.Helpers.DragHelper
import com.example.hogwartsasiermartinez.databinding.ActivityMainBinding
import com.example.hogwartsasiermartinez.databinding.ActivityRegistroBinding
import com.example.hogwartsasiermartinez.model.Casa
import com.example.hogwartsasiermartinez.model.Usuario
import com.example.hogwartsasiermartinez.viewModel.UsuarioViewModel
import kotlin.getValue

class activity_registro : AppCompatActivity() {

    lateinit var binding: ActivityRegistroBinding

    private val viewModel : UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerCasas = binding.recyclerCasas

        val casas = mutableListOf(
            Casa(1, "Gryffindor", 0),
            Casa(2, "Slytherin", 0),
            Casa(3, "Ravenclaw", 0),
            Casa(4, "Hufflepuff", 0)
        )

        val adapter = CasasAdapter(casas)
        recyclerCasas.adapter = adapter
        recyclerCasas.layoutManager = LinearLayoutManager(this)

        val touchHelper = ItemTouchHelper(DragHelper(adapter))
        touchHelper.attachToRecyclerView(recyclerCasas)

        binding.recyclerCasas.layoutManager = LinearLayoutManager(this)

        binding.btRegistro.setOnClickListener {
            var nombre = binding.etNombreReg.text.toString()
            var passwd = binding.etPasswdReg.text.toString()
            val ordenFinalIds = adapter.getOrdenCasasId() // lista de IDs
            viewModel.selectHouse(ordenFinalIds)

            viewModel.casaSeleccionadaId.observe(this) { casaId ->
                if (casaId != null) {
                    val usuario = Usuario(
                        nombre = nombre,
                        password = passwd,
                        experiencia = 0,
                        nivel = 1,
                        casa_id = casaId
                    )
                    viewModel.addUser(usuario)
                }

                var intentVAdmin = Intent(this, activity_sombrero::class.java)
                intent.putExtra("casaNombre", casaId)
                startActivity(intentVAdmin)
            }
        }

    }
}
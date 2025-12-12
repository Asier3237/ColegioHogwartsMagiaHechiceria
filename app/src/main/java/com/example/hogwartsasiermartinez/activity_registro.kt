package com.example.hogwartsasiermartinez

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hogwartsasiermartinez.Adapters.CasasAdapter
import com.example.hogwartsasiermartinez.Auxiliar.Sesion
import com.example.hogwartsasiermartinez.Helpers.DragHelper
import com.example.hogwartsasiermartinez.databinding.ActivityRegistroBinding
import com.example.hogwartsasiermartinez.model.Usuario
import com.example.hogwartsasiermartinez.viewModel.CasasViewModel
import com.example.hogwartsasiermartinez.viewModel.UsuarioViewModel

class activity_registro : AppCompatActivity() {

    lateinit var binding: ActivityRegistroBinding

    private val viewModel: UsuarioViewModel by viewModels()
    private val casasViewModel: CasasViewModel by viewModels()

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

        // preparamos el adapter para la lista de casas
        val adapter = CasasAdapter(mutableListOf())
        binding.recyclerCasas.adapter = adapter
        binding.recyclerCasas.layoutManager = LinearLayoutManager(this)

        // instanciamos el helper para poder arrastrar y soltar filas
        val touchHelper = ItemTouchHelper(DragHelper(adapter))
        touchHelper.attachToRecyclerView(binding.recyclerCasas)

        // observamos el livedata de las casas para cuando lleguen los datos
        casasViewModel.casasLiveData.observe(this) { casas ->
            Log.d("Registro", "Casas recibidas: ${casas.size}")
            // cuando llegan, limpiamos la lista y metemos las nuevas mpor si cambia algo respecto a las anteriores
            adapter.casas.clear()
            adapter.casas.addAll(casas)
            adapter.notifyDataSetChanged() // se notifica al adapter para repintar la lista
        }

        casasViewModel.cargarCasas()

        binding.btRegistro.setOnClickListener {
            val nombre = binding.etNombreReg.text.toString()
            val passwd = binding.etPasswdReg.text.toString()
            // pillamos el orden final en el que el usuario ha dejado las casas
            val ordenFinalIds = adapter.getOrdenCasasId()

            // mandamos las preferencias a la api y guardamos los datos
            viewModel.selectHouse(ordenFinalIds)
            viewModel.nombreAux = nombre
            viewModel.passwdAux = passwd
        }

        // observamos la respuesta del sombrero, que nos devuelve el id de la casa
        viewModel.casaSeleccionadaId.observe(this) { casaId ->
            if (casaId != null && casaId > 0) {
                // creamos el objeto usuario con todos los datos
                val usuario = Usuario(
                    nombre = viewModel.nombreAux.toString(),
                    password = viewModel.passwdAux.toString(),
                    experiencia = 0,
                    nivel = 1,
                    casa_id = casaId
                )
                viewModel.addUser(usuario)

                // guardamos el id de la casa en la sesión para usarlo después
                Sesion.casaId = casaId

                val intentVSombrero = Intent(this, activity_sombrero::class.java)
                startActivity(intentVSombrero)
            }
        }
    }
}
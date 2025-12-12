package com.example.hogwartsasiermartinez

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.hogwartsasiermartinez.Auxiliar.Sesion
import com.example.hogwartsasiermartinez.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private var usuarioId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // se pone el color de la casa del login para darle color a la interfaz
        if (Sesion.colorCasa.isNotEmpty()) {
            try {
                val color = Color.parseColor(Sesion.colorCasa)
                binding.toolbar.setBackgroundColor(color)
                window.statusBarColor = color
                val headerView = binding.navView.getHeaderView(0)
                headerView.setBackgroundColor(color)
            } catch (e: IllegalArgumentException) {

            }
        }

        setSupportActionBar(binding.toolbar)

        // conecto la toolbar con el menú lateral para que aparezca el botón del menu hamburguesa
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            0,
            0
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // pillo el id del usuario que ha iniciado sesión
        usuarioId = Sesion.usuarioId

        // escondo las opciones del menú lateral que ahora están en la bottomNavigation
        val menu = binding.navView.menu
        menu.findItem(R.id.nav_usuarios).isVisible = false
        menu.findItem(R.id.nav_asignaturas).isVisible = false
        menu.findItem(R.id.nav_pociones).isVisible = false
        menu.findItem(R.id.nav_hechizos).isVisible = false

        // cuando la pantalla arranca por primera vez, pongo el fragmento de usuarios por defecto
        if (savedInstanceState == null) {
            replaceFragment(FragmentoUsuarios())
            binding.bottomNavView.selectedItemId = R.id.nav_usuarios
        }

        // la lógica para el menú lateral
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_ranking -> {
                    replaceFragment(FragmentoRankingCasas())
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        //la lógica de la bottomNavigation
        binding.bottomNavView.setOnItemSelectedListener { item ->
            // según el botón que se pulse, cargo un fragmento u otro
            val fragment = when (item.itemId) {
                R.id.nav_usuarios -> FragmentoUsuarios()
                R.id.nav_asignaturas -> FragmentoAsignaturas()
                R.id.nav_pociones -> FragmentoPociones()
                R.id.nav_hechizos -> FragmentoHechizos()
                else -> null
            }
            if (fragment != null) {
                replaceFragment(fragment)
                true
            } else {
                false
            }
        }

        val imgPerfil = binding.toolbar.findViewById<ImageView>(R.id.imgPerfil)
        imgPerfil.setOnClickListener {
            //el fragmento sabe el id ya que se guardó en el objeto sesion
            val fragment = FragmentoPerfil()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        val headerView = binding.navView.getHeaderView(0)
        val tvRol = headerView.findViewById<TextView>(R.id.tvRol)
        tvRol.text = "Administrador"
    }

    // permite no repetir el código de cambiar de fragmento
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
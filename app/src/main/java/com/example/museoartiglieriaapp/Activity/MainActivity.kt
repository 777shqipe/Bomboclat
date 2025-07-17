package com.example.museoartiglieriaapp.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.museoartiglieriaapp.CameraFragment
import com.example.museoartiglieriaapp.R
import com.example.museoartiglieriaapp.databinding.ActivityMainBinding // Assicurati che il nome sia corretto (di solito generato da activity_main.xml)
import com.example.museoartiglieriaapp.Fragments.HomeFragment
import com.example.museoartiglieriaapp.Fragments.InfoFragment
import com.example.museoartiglieriaapp.Fragments.StorageFragment
import com.example.museoartiglieriaapp.Fragments.TicketsFragment
// Rimuovi import non usati come BottomNavigationView e FloatingActionButton se accedi solo tramite binding

class MainActivity : AppCompatActivity() {

    // Dichiarazione del binding a livello di classe
    private lateinit var binding: ActivityMainBinding // Il nome della classe binding dipende dal nome del tuo file XML (activity_main.xml -> ActivityMainBinding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Abilita la visualizzazione edge-to-edge

        // 1. Inizializza il binding e imposta il contentView UNA SOLA VOLTA
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // Usa la root del binding

        // 2. Applica i listener per gli insets della finestra al layout principale
        // Usa binding.mainActivityRoot se 'main_activity_root' è l'ID del root layout in activity_main.xml
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainActivityRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Carica il HomeFragment di default solo la prima volta e imposta l'item selezionato
        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.navigation_home // Seleziona l'item home
            loadFragment(HomeFragment(), "HOME_FRAGMENT_TAG", false)
        }

        // 4. Imposta il listener per la BottomNavigationView accedendo tramite binding
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val selectedFragment: Fragment? = when (menuItem.itemId) {
                R.id.navigation_home -> HomeFragment()
                R.id.navigation_tickets -> TicketsFragment()
                R.id.navigation_files -> StorageFragment()
                R.id.navigation_location -> InfoFragment()
                else -> null // Non dovrebbe accadere se gli ID del menu sono corretti
            }

            if (selectedFragment != null) {
                // Determina un tag univoco per il fragment se necessario
                val tag = selectedFragment::class.java.simpleName + "_TAG"
                loadFragment(selectedFragment, tag, false) // Generalmente non aggiungere al backstack per bottom nav
                true // Indica che l'item è stato gestito
            } else {
                false // L'item non è stato gestito
            }
        }
        binding.fabCamera.setOnClickListener {
            loadFragment(CameraFragment(), "CAMERA_FRAGMENT_TAG", true) // Aggiungi al backstack se vuoi poter tornare indietro
        }

    }

    /**
     * Funzione helper per caricare i Fragment nel container.
     */
    private fun loadFragment(fragment: Fragment, tag: String?, addToBackStack: Boolean = true) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        // Evita di ricaricare lo stesso fragment se è già visualizzato
        if (currentFragment != null && currentFragment::class == fragment::class) { // Semplificato il controllo
            return
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment, tag)

        if (addToBackStack) {
            transaction.addToBackStack(tag)
        }
        transaction.commit()
    }
}
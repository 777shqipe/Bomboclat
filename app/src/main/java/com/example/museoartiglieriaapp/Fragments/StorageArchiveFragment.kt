package com.example.museoartiglieriaapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.museoartiglieriaapp.R

class StorageArchiveFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_storage_archive, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mappa nome categoria -> url immagine
        val artifactImageUrls = mapOf(
            "TANKS" to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0d/Usht-Steam-Tank-Tracklaying.jpg/640px-Usht-Steam-Tank-Tracklaying.jpg",
            "PORTABLE_FIREARMS" to "https://i0.wp.com/laststandonzombieisland.com/wp-content/uploads/2023/10/ardinti-jager-austrian-kuk-1917-mountin-alpine-maximilian-von-poosch-gablenz-1872-1968-hgm-vienna.jpg?fit=1800,1261&ssl=1",
            "EDGED_WEAPONS" to "https://edgedweapons.nl/images/slideshow/slider-4.jpg",
            "MILITARY_EQUIPMENT" to "https://images-cdn.bridgemanimages.com/api/1.0/image/150.ETE.43307840.7055475/4867155.jpg"
        )

// Carico le immagini nelle card
        Glide.with(this)
            .load(artifactImageUrls["TANKS"])
            .placeholder(R.drawable.placeholder_shape)
            .into(view.findViewById(R.id.image_tanks))
        Glide.with(this)
            .load(artifactImageUrls["PORTABLE_FIREARMS"])
            .placeholder(R.drawable.placeholder_shape)
            .into(view.findViewById(R.id.image_portable_firearms))
        Glide.with(this)
            .load(artifactImageUrls["EDGED_WEAPONS"])
            .placeholder(R.drawable.placeholder_shape)
            .into(view.findViewById(R.id.image_edged_weapons))
        Glide.with(this)
            .load(artifactImageUrls["MILITARY_EQUIPMENT"])
            .placeholder(R.drawable.placeholder_shape)
            .into(view.findViewById(R.id.image_military_equipment))



        // Listener per le 4 card
        view.findViewById<View>(R.id.card1).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CategoryListFragment.newInstance("tanks"))
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<View>(R.id.card2).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CategoryListFragment.newInstance("firearms"))
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<View>(R.id.card4).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CategoryListFragment.newInstance("edged_weapons"))
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<View>(R.id.card5).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CategoryListFragment.newInstance("military_equipment"))
                .addToBackStack(null)
                .commit()
        }
    }
} 
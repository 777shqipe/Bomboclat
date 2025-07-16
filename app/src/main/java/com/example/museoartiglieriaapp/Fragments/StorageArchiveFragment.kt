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
            "TANKS" to "https://images8.alphacoders.com/673/673092.png",
            "PORTABLE_FIREARMS" to "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcSgFMrKsLjcqgaN9RzKxYM30ChJf2DdW4-_FTADBx4-mlYk3yen",
            "EDGED_WEAPONS" to "https://www.kubel1943.it/foto/pompire.jpg",
            "MILITARY_EQUIPMENT" to "https://www.militariainroma.com/wp-content/uploads/2025/04/IMG_9350.jpeg"
        )

        // Carico le immagini nelle card
        Glide.with(this)
            .load(artifactImageUrls["TANKS"])
            .placeholder(R.drawable.placeholder_shape)
            .into(view.findViewById(R.id.image_card1))
        Glide.with(this)
            .load(artifactImageUrls["PORTABLE_FIREARMS"])
            .placeholder(R.drawable.placeholder_shape)
            .into(view.findViewById(R.id.image_card2))
        Glide.with(this)
            .load(artifactImageUrls["EDGED_WEAPONS"])
            .placeholder(R.drawable.placeholder_shape)
            .into(view.findViewById(R.id.image_card4))
        Glide.with(this)
            .load(artifactImageUrls["MILITARY_EQUIPMENT"])
            .placeholder(R.drawable.placeholder_shape)
            .into(view.findViewById(R.id.image_card5))

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
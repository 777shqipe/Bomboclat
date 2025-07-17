package com.example.museoartiglieriaapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.museoartiglieriaapp.R
import com.bumptech.glide.Glide

class TankDetailFragment : Fragment() {
    companion object {
        private const val ARG_TANK = "arg_tank"
        fun newInstance(tank: Tank): TankDetailFragment {
            val fragment = TankDetailFragment()
            val args = Bundle()
            args.putParcelable(ARG_TANK, tank)
            fragment.arguments = args
            return fragment
        }
    }

    private var tank: Tank? = null
    private val artifactImageUrls = mapOf(
        "PANZER IV" to "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiQXVrDDkJ4PLTZK9F28lYTsRY8uFxA-Lq4TRXBGVMthRDsNLcQ_VykVgN-7fkA2ofhY7IP0Xe4TH3m0D9mdSPEZ45gs1xfxbd5gsejBhPqScVHcOSaYIcJUhmEzxLSawFVg3ifZiLLkB9I/s1600/0+0+0+42100076342_f653e3d6da_b.jpg",
        "Panzer IV" to "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiQXVrDDkJ4PLTZK9F28lYTsRY8uFxA-Lq4TRXBGVMthRDsNLcQ_VykVgN-7fkA2ofhY7IP0Xe4TH3m0D9mdSPEZ45gs1xfxbd5gsejBhPqScVHcOSaYIcJUhmEzxLSawFVg3ifZiLLkB9I/s1600/0+0+0+42100076342_f653e3d6da_b.jpg",
        "TIGER I" to "https://upload.wikimedia.org/wikipedia/commons/6/6e/Bundesarchiv_Bild_101I-299-1805-16%2C_Nordfrankreich%2C_Panzer_VI_%28Tiger_I%29_cropped.jpg",
        "Tiger I" to "https://upload.wikimedia.org/wikipedia/commons/6/6e/Bundesarchiv_Bild_101I-299-1805-16%2C_Nordfrankreich%2C_Panzer_VI_%28Tiger_I%29_cropped.jpg",
        "IS-2" to "https://preview.redd.it/future-tanks-v0-8lkc7e051qia1.jpg?width=320&crop=smart&auto=webp&s=fcb17cbc659034c89317dd2715802e034526c908",
        "MARK I" to "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiENouirmJo3DeyiHP4cSOU_EyrzMMoBzGXgl-IuOdup8Ih1Kxo34pgzDd7qaoWxBQucWXil1mPN-TJiO1RUv880xB-0EIvWVmWu1gXBWMKI7PbfKU6aQxa1W4nvaWxwfckU1mTlYDLnJSg/s1600/carro+armato+con+scritta+in+russo.jpg",
        "Mark I" to "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiENouirmJo3DeyiHP4cSOU_EyrzMMoBzGXgl-IuOdup8Ih1Kxo34pgzDd7qaoWxBQucWXil1mPN-TJiO1RUv880xB-0EIvWVmWu1gXBWMKI7PbfKU6aQxa1W4nvaWxwfckU1mTlYDLnJSg/s1600/carro+armato+con+scritta+in+russo.jpg",
        "M13/40" to "https://www.lasecondaguerramondiale.org/wp-content/uploads/2019/08/91c14cbfa735c2522cedce0eda8810b9.jpg",
        "T-34/85" to "https://eu-wotp.wgcdn.co/dcont/fb/image/t-34-85-austrian-army-museum.jpg"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tank = arguments?.getParcelable(ARG_TANK)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tank_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView = view.findViewById<ImageView>(R.id.tank_detail_image)
        val nameView = view.findViewById<TextView>(R.id.tank_detail_name)
        val originView = view.findViewById<TextView>(R.id.tank_detail_origin)
        val yearView = view.findViewById<TextView>(R.id.tank_detail_year)
        val useView = view.findViewById<TextView>(R.id.tank_detail_use)
        val firstAppView = view.findViewById<TextView>(R.id.tank_detail_first_appearance)
        val historyView = view.findViewById<TextView>(R.id.tank_detail_brief_history)

        tank?.let {
            // Provo prima con il nome esatto, poi con la normalizzazione
            var imageUrl = artifactImageUrls[it.name]
            if (imageUrl == null) {
                // Se non trova il nome esatto, prova con la normalizzazione
                val normalizedImageUrls = artifactImageUrls.mapKeys { normalizeName(it.key) }
                val normalizedName = normalizeName(it.name)
                imageUrl = normalizedImageUrls[normalizedName]
            }
            
            if (imageUrl != null && imageUrl.isNotEmpty()) {
                Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_shape)
                    .error(R.drawable.placeholder_shape)
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.placeholder_shape)
            }
            nameView.text = it.name
            originView.text = "Origin: ${it.origin}"
            yearView.text = "Year: ${it.yearOfProduction}"
            useView.text = "Use: ${it.historicalUse}"
            firstAppView.text = "First Appearance: ${it.firstAppearance}"
            historyView.text = it.briefHistory
        }
    }

    private fun normalizeName(name: String): String {
        return name.trim()
            .replace(Regex("[\\s\\-_/]+"), "_")
            .lowercase()
            .replace(Regex("[^a-z0-9_]"), "")
    }
} 
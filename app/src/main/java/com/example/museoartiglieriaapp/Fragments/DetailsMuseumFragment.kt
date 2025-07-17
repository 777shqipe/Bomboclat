package com.example.museoartiglieriaapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.museoartiglieriaapp.R
import android.speech.tts.TextToSpeech
import android.widget.TextView
import android.widget.ImageView
import java.util.Locale


class DetailsMuseumFragment : Fragment() {


    private var tts: TextToSpeech? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details_museum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gestione del pulsante di chiusura
        view.findViewById<ImageButton>(R.id.close_button)?.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        view.findViewById<Button>(R.id.visit_archive_button)?.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StorageArchiveFragment())
                .addToBackStack(null)
                .commit()
        }

        // Inizializzazione delle view per il TTS
        val ttsButton = view.findViewById<ImageButton>(R.id.ttsButton)
        val title = view.findViewById<TextView>(R.id.museum_title)
        val address = view.findViewById<TextView>(R.id.museum_address)
        val desc1 = view.findViewById<TextView>(R.id.description_part1)
        val desc2 = view.findViewById<TextView>(R.id.description_part2)
        val desc3 = view.findViewById<TextView>(R.id.description_part3)

        // Inizializzazione del TTS
        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.ITALIAN
            }
        }

        // Gestione del click sul bottone TTS
        ttsButton.setOnClickListener {
            val info = StringBuilder()
            info.append("${title.text}. ")
            info.append("${address.text}. ")
            info.append("${desc1.text}. ")
            info.append("${desc2.text}. ")
            info.append("${desc3.text}.")
            tts?.speak(info.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
        }

        // Gestione della mappa zoomabile
        val mapImage = view.findViewById<ImageView>(R.id.guide_map_image)
        mapImage.setOnClickListener {
            DialogMapZoom(R.drawable.mappa_ferraris).show(parentFragmentManager, "zoomMap")
        }
    }

    override fun onDestroyView() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroyView()
    }


}
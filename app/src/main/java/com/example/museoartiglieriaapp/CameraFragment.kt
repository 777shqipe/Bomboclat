package com.example.museoartiglieriaapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.example.museoartiglieriaapp.Models.PortableFirearm
import com.example.museoartiglieriaapp.Fragments.PortableFirearmDetailFragment
import com.example.museoartiglieriaapp.Repository.StorageRepository

class CameraFragment : Fragment() {
    private lateinit var barcodeView: CompoundBarcodeView
    private var lastScanned: String? = null

    // Mappa QR code -> nome artefatto
    private val qrCodeToArtifact = mapOf(
        "PANZER_IV" to "Panzer IV",
        "TIGER_I" to "Tiger I", 
        "IS_2" to "IS-2",
        "MARK_I" to "Mark I",
        "M13_40" to "M13/40",
        "T34_85" to "T-34/85",
        "MP40" to "MP40",
        "M91_30" to "M91/30",
        "STEN_GUN" to "STEN GUN",
        "M1934" to "M1934",
        "M91" to "M91",
        "M1A1" to "M1A1",
        "HELMET" to "HELMET",
        "M33" to "M33",
        "SS_COAT" to "SS Coat",
        "M1" to "M1",
        "BRODIE" to "BRODIE",
        "USHANKA" to "Ushanka",
        "LEBEL" to "Lebel",
        "SEITEN" to "Seitengewehr",
        "1907" to "1907",
        "M1891" to "M1891",
        "M3" to "M3",
        "NKM" to "Nkm"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        barcodeView = view.findViewById(R.id.barcode_scanner)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (allPermissionsGranted()) {
            startScanner()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                10
            )
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun startScanner() {
        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                val qr = result?.text
                if (qr != null && qr != lastScanned) {
                    lastScanned = qr
                    handleQRCode(qr)
                }
            }
        })
        barcodeView.resume()
    }

    private fun handleQRCode(qrCode: String) {
        // Cerca l'artefatto corrispondente al QR code
        val artifactName = qrCodeToArtifact[qrCode]
        
        if (artifactName != null) {
            // Trova l'artefatto nel repository
            val allItems = mutableListOf<com.example.museoartiglieriaapp.Models.StorageItem>()
            allItems.addAll(StorageRepository.getItemsByCategory("tanks"))
            allItems.addAll(StorageRepository.getItemsByCategory("firearms"))
            allItems.addAll(StorageRepository.getItemsByCategory("military_equipment"))
            allItems.addAll(StorageRepository.getItemsByCategory("edged_weapons"))
            
            val artifact = allItems.find { it.name == artifactName }
            
            if (artifact != null) {
                // Converti StorageItem in PortableFirearm
                val yearInt = artifact.yearOfProduction.toIntOrNull() ?: 0
                val firearm = PortableFirearm(
                    id = artifact.id,
                    name = artifact.name,
                    image = artifact.image,
                    origin = artifact.origin,
                    yearOfProduction = yearInt,
                    historicalUse = artifact.historicalUse,
                    firstAppearance = artifact.firstAppearance,
                    briefHistory = artifact.briefHistory,
                    trivia = artifact.trivia,
                    technicalSpecifications = artifact.technicalSpecifications
                )
                
                // Apri la schermata di dettaglio
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PortableFirearmDetailFragment.newInstance(firearm))
                    .addToBackStack(null)
                    .commit()
                
                Toast.makeText(requireContext(), "Artefatto trovato: $artifactName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Artefatto non trovato: $artifactName", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "QR code non riconosciuto: $qrCode", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::barcodeView.isInitialized) barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        if (::barcodeView.isInitialized) barcodeView.pause()
    }
}


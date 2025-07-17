package com.example.museoartiglieriaapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.museoartiglieriaapp.Adapter.StorageItemAdapter
import com.example.museoartiglieriaapp.Models.StorageItem
import com.example.museoartiglieriaapp.Models.PortableFirearm
import com.example.museoartiglieriaapp.R
import com.example.museoartiglieriaapp.Repository.StorageRepository

class CategoryListFragment : Fragment() {
    companion object {
        private const val ARG_CATEGORY = "arg_category"
        
        fun newInstance(category: String): CategoryListFragment {
            val fragment = CategoryListFragment()
            val args = Bundle()
            args.putString(ARG_CATEGORY, category)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Gestione del pulsante indietro
        view.findViewById<View>(R.id.backButton)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        
        val category = arguments?.getString(ARG_CATEGORY) ?: "tanks"
        val items = StorageRepository.getItemsByCategory(category)
        
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewCategory)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = StorageItemAdapter(items) { item ->
            // Conversione StorageItem -> PortableFirearm
            val yearInt = item.yearOfProduction.toIntOrNull() ?: 0
            val firearm = PortableFirearm(
                id = item.id,
                name = item.name,
                image = item.image,
                origin = item.origin,
                yearOfProduction = yearInt,
                historicalUse = item.historicalUse,
                firstAppearance = item.firstAppearance,
                briefHistory = item.briefHistory,
                trivia = item.trivia,
                technicalSpecifications = item.technicalSpecifications
            )
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PortableFirearmDetailFragment.newInstance(firearm))
                .addToBackStack(null)
                .commit()
        }
    }
} 
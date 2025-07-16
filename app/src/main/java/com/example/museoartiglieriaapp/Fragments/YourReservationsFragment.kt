package com.example.museoartiglieriaapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.museoartiglieriaapp.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.museoartiglieriaapp.Model.ReservationRepository
import com.example.museoartiglieriaapp.Model.Reservation
import android.widget.ImageView

class YourReservationsFragment : Fragment() {
    private lateinit var adapter: ReservationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_your_reservations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gestione del pulsante indietro
        view.findViewById<View>(R.id.backButton)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.reservationsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ReservationAdapter()
        recyclerView.adapter = adapter
        adapter.updateReservations(ReservationRepository.getReservations())
    }

    override fun onResume() {
        super.onResume()
        adapter.updateReservations(ReservationRepository.getReservations())
    }

    class ReservationAdapter : RecyclerView.Adapter<ReservationViewHolder>() {
        private var reservations: List<Reservation> = emptyList()
        fun updateReservations(newList: List<Reservation>) {
            reservations = newList
            notifyDataSetChanged()
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_reservation, parent, false)
            return ReservationViewHolder(v)
        }
        override fun getItemCount() = reservations.size
        override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
            val res = reservations[position]
            holder.ticketTypeText.text = res.ticketType
            holder.timeText.text = res.time
            holder.locationText.text = res.location
            holder.dateText.text = res.date
            val imageRes = holder.itemView.context.resources.getIdentifier(res.ticketImage, "drawable", holder.itemView.context.packageName)
            holder.ticketImage.setImageResource(imageRes)
        }
    }

    class ReservationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ticketTypeText: TextView = view.findViewById(R.id.ticketTypeText)
        val timeText: TextView = view.findViewById(R.id.timeText)
        val locationText: TextView = view.findViewById(R.id.locationText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val ticketImage: ImageView = view.findViewById(R.id.ticketImage)
    }
}

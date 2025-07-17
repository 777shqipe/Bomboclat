package com.example.museoartiglieriaapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.museoartiglieriaapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import androidx.core.content.ContextCompat
import android.widget.Toast
import android.widget.TextView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.example.museoartiglieriaapp.Model.ReservationRepository
import com.example.museoartiglieriaapp.Model.Reservation
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale
import android.widget.DatePicker

class NewReservationFragment : Fragment() {
    private var selectedTicketType: String? = null
    private var selectedTime: String? = null
    private var selectedLocation: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_newreservation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nameInput = view.findViewById<android.widget.EditText>(R.id.nameInput)
        val emailInput = view.findViewById<android.widget.EditText>(R.id.emailInput)
        val phoneInput = view.findViewById<android.widget.EditText>(R.id.phoneInput)
        val scrollView = view.findViewById<android.widget.ScrollView>(R.id.my_scroll)
        
        // Gestione tastiera per i campi di input
        val inputFields = listOf(nameInput, emailInput, phoneInput)
        inputFields.forEach { editText ->
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    // Scrolla al campo quando riceve il focus con calcolo più preciso
                    scrollView.post {
                        val editTextLocation = IntArray(2)
                        editText.getLocationInWindow(editTextLocation)
                        val editTextTop = editTextLocation[1]
                        
                        // Calcola la posizione ottimale per mostrare il campo
                        val screenHeight = resources.displayMetrics.heightPixels
                        val keyboardHeight = (screenHeight * 0.4).toInt() // Stima altezza tastiera
                        val availableHeight = screenHeight - keyboardHeight
                        
                        // Più spazio per il campo del telefono (ultimo campo)
                        val targetPosition = if (editText.id == R.id.phoneInput) {
                            editTextTop - (availableHeight / 4) // Ancora più spazio per il telefono
                        } else {
                            editTextTop - (availableHeight / 3) // Spazio normale per gli altri
                        }
                        
                        // Assicurati che non scrolli troppo in alto
                        val maxScroll = scrollView.getChildAt(0).height - scrollView.height
                        val finalPosition = if (targetPosition < 0) 0 else minOf(targetPosition, maxScroll)
                        
                        scrollView.smoothScrollTo(0, finalPosition)
                    }
                }
            }
        }
        
        // Listener aggiuntivo per gestire meglio la tastiera
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = android.graphics.Rect()
            view.getWindowVisibleDisplayFrame(r)
            val screenHeight = view.rootView.height
            val keyboardHeight = screenHeight - r.bottom
            
            if (keyboardHeight > screenHeight * 0.15) {
                // Tastiera è visibile, assicurati che il campo attivo sia visibile
                val focusedView = view.findFocus()
                if (focusedView is android.widget.EditText) {
                    scrollView.post {
                        val editTextLocation = IntArray(2)
                        focusedView.getLocationInWindow(editTextLocation)
                        val editTextTop = editTextLocation[1]
                        val targetPosition = editTextTop - keyboardHeight - 200 // Più spazio sopra
                        
                        // Assicurati che non scrolli troppo in alto
                        val maxScroll = scrollView.getChildAt(0).height - scrollView.height
                        val finalPosition = if (targetPosition < 0) 0 else minOf(targetPosition, maxScroll)
                        
                        scrollView.smoothScrollTo(0, finalPosition)
                    }
                }
            }
        }

        view.findViewById<MaterialButton>(R.id.checkoutButton).setOnClickListener {
            val name = nameInput.text?.toString()?.trim() ?: ""
            val email = emailInput.text?.toString()?.trim() ?: ""
            val phone = phoneInput.text?.toString()?.trim() ?: ""
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "Compila tutti i campi obbligatori!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedTicketType == null) {
                Toast.makeText(requireContext(), "Seleziona un tipo di biglietto!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedTime == null) {
                Toast.makeText(requireContext(), "Seleziona un orario!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedLocation == null) {
                Toast.makeText(requireContext(), "Seleziona un museo!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val datePicker = view.findViewById<DatePicker>(R.id.date_picker)
            val selectedDate = getSelectedDate(datePicker)
            if (selectedDate.isEmpty()) {
                Toast.makeText(requireContext(), "Seleziona una data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PaymentFragment())
                .addToBackStack(null)
                .commit()
        }

        val cardSingle = view.findViewById<MaterialCardView>(R.id.card_single)
        val cardFamily = view.findViewById<MaterialCardView>(R.id.card_family)
        val cardVip = view.findViewById<MaterialCardView>(R.id.card_vip)
        val defaultColor = ContextCompat.getColor(requireContext(), R.color.grey_button_background)
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.button_time_selector)

        val cards = listOf(cardSingle, cardFamily, cardVip)
        val textColorDefault = ContextCompat.getColor(requireContext(), R.color.black)
        val textColorSelected = ContextCompat.getColor(requireContext(), R.color.white)

        // Card selection logic
        val cardSingleText = cardSingle.findViewById<TextView>(R.id.card_single_text)
        val cardSinglePrice = cardSingle.findViewById<TextView>(R.id.card_single_price)
        val cardFamilyText = cardFamily.findViewById<TextView>(R.id.card_family_text)
        val cardFamilyPrice = cardFamily.findViewById<TextView>(R.id.card_family_price)
        val cardVipText = cardVip.findViewById<TextView>(R.id.card_vip_text)
        val cardVipPrice = cardVip.findViewById<TextView>(R.id.card_vip_price)
        Log.d("DEBUG", "cardSingleText: $cardSingleText, cardSinglePrice: $cardSinglePrice, cardFamilyText: $cardFamilyText, cardFamilyPrice: $cardFamilyPrice, cardVipText: $cardVipText, cardVipPrice: $cardVipPrice")
        if (cardSingleText == null || cardSinglePrice == null || cardFamilyText == null || cardFamilyPrice == null || cardVipText == null || cardVipPrice == null) {
            Toast.makeText(requireContext(), "Errore: uno o più TextView delle card non trovati!", Toast.LENGTH_LONG).show()
            return
        }
        val cardTextViews = listOf(cardSingleText, cardSinglePrice, cardFamilyText, cardFamilyPrice, cardVipText, cardVipPrice)

        fun setCardTextColor(selectedCard: MaterialCardView) {
            val allCards = listOf(cardSingle, cardFamily, cardVip)
            allCards.forEach { card ->
                val t1 = card.findViewById<TextView>(R.id.card_single_text) ?: card.findViewById<TextView>(R.id.card_family_text) ?: card.findViewById<TextView>(R.id.card_vip_text)
                val t2 = card.findViewById<TextView>(R.id.card_single_price) ?: card.findViewById<TextView>(R.id.card_family_price) ?: card.findViewById<TextView>(R.id.card_vip_price)
                t1?.setTextColor(textColorDefault)
                t2?.setTextColor(textColorDefault)
            }
            val t1 = selectedCard.findViewById<TextView>(R.id.card_single_text) ?: selectedCard.findViewById<TextView>(R.id.card_family_text) ?: selectedCard.findViewById<TextView>(R.id.card_vip_text)
            val t2 = selectedCard.findViewById<TextView>(R.id.card_single_price) ?: selectedCard.findViewById<TextView>(R.id.card_family_price) ?: selectedCard.findViewById<TextView>(R.id.card_vip_price)
            t1?.setTextColor(textColorSelected)
            t2?.setTextColor(textColorSelected)
        }

        for ((i, card) in cards.withIndex()) {
            card.setOnClickListener {
                cards.forEach { it.setCardBackgroundColor(defaultColor) }
                card.setCardBackgroundColor(selectedColor)
                setCardTextColor(card)
                selectedTicketType = when (i) {
                    0 -> "Single"
                    1 -> "Family"
                    2 -> "VIP"
                    else -> null
                }
                val datePicker = view.findViewById<DatePicker>(R.id.date_picker)
                ReservationRepository.draft = Reservation(
                    ticketType = selectedTicketType ?: "",
                    time = selectedTime ?: "",
                    location = selectedLocation ?: "",
                    date = getSelectedDate(datePicker),
                    ticketImage = getTicketImage(selectedTicketType)
                )
            }
        }
        // Imposta colore testo default all'avvio
        cards.forEach { card ->
            val t1 = card.findViewById<TextView>(R.id.card_single_text) ?: card.findViewById<TextView>(R.id.card_family_text) ?: card.findViewById<TextView>(R.id.card_vip_text)
            val t2 = card.findViewById<TextView>(R.id.card_single_price) ?: card.findViewById<TextView>(R.id.card_family_price) ?: card.findViewById<TextView>(R.id.card_vip_price)
            t1?.setTextColor(textColorDefault)
            t2?.setTextColor(textColorDefault)
        }
        setCardTextColor(cardSingle)

        // Logica selezione orari
        val timeGrid = view.findViewById<android.widget.GridLayout>(R.id.timeGrid)
        val timeButtons = mutableListOf<MaterialButton>()
        for (i in 0 until timeGrid.childCount) {
            val child = timeGrid.getChildAt(i)
            if (child is MaterialButton) {
                timeButtons.add(child)
            }
        }
        timeButtons.forEach { btn ->
            btn.setOnClickListener {
                timeButtons.forEach {
                    it.setBackgroundColor(defaultColor)
                    it.setTextColor(textColorDefault)
                }
                btn.setBackgroundColor(selectedColor)
                btn.setTextColor(textColorSelected)
                selectedTime = btn.text.toString()
                val datePicker = view.findViewById<DatePicker>(R.id.date_picker)
                ReservationRepository.draft = Reservation(
                    ticketType = selectedTicketType ?: "",
                    time = selectedTime ?: "",
                    location = selectedLocation ?: "",
                    date = getSelectedDate(datePicker),
                    ticketImage = getTicketImage(selectedTicketType)
                )
            }
        }
        // Imposta colore testo default all'avvio
        timeButtons.forEach { it.setTextColor(textColorDefault) }

        // Dropdown location logic
        val dropdownButton = view.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.dropdownButton)
        val dropdownMenu = view.findViewById<com.google.android.material.card.MaterialCardView>(R.id.dropdownMenu)
        val dropdownTitle = view.findViewById<TextView>(R.id.dropdownTitle)
        val arrowIcon = view.findViewById<ImageView>(R.id.arrowIcon)
        val location1 = view.findViewById<TextView>(R.id.location1)
        val location2 = view.findViewById<TextView>(R.id.location2)

        dropdownButton.setOnClickListener {
            if (dropdownMenu.isVisible) {
                arrowIcon.setImageResource(R.drawable.ic_arrow_up)
                dropdownMenu.visibility = View.GONE
            } else {
                arrowIcon.setImageResource(R.drawable.ic_arrow_down)
                dropdownMenu.visibility = View.VISIBLE
            }
        }

        val locationViews = listOf(location1, location2)
        val locationClickListener = View.OnClickListener { v ->
            val loc = (v as TextView).text.toString()
            selectedLocation = loc
            dropdownTitle.text = loc
            dropdownMenu.visibility = View.GONE
            arrowIcon.setImageResource(R.drawable.ic_arrow_up)
            locationViews.forEach { it.isSelected = false }
            v.isSelected = true
            val datePicker = view.findViewById<DatePicker>(R.id.date_picker)
            ReservationRepository.draft = Reservation(
                ticketType = selectedTicketType ?: "",
                time = selectedTime ?: "",
                location = selectedLocation ?: "",
                date = getSelectedDate(datePicker),
                ticketImage = getTicketImage(selectedTicketType)
            )
        }
        location1.setOnClickListener(locationClickListener)
        location2.setOnClickListener(locationClickListener)

        view.findViewById<View>(R.id.backButton)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        val view = view ?: return
        val cardSingle = view.findViewById<MaterialCardView>(R.id.card_single)
        val cardFamily = view.findViewById<MaterialCardView>(R.id.card_family)
        val cardVip = view.findViewById<MaterialCardView>(R.id.card_vip)
        val cards = listOf(cardSingle, cardFamily, cardVip)
        val textColorDefault = ContextCompat.getColor(requireContext(), R.color.black)
        val textColorSelected = ContextCompat.getColor(requireContext(), R.color.white)
        
        // Duplico la logica di setCardTextColor
        fun setCardTextColor(selectedCard: MaterialCardView) {
            val allCards = listOf(cardSingle, cardFamily, cardVip)
            allCards.forEach { card ->
                val t1 = card.findViewById<TextView>(R.id.card_single_text) ?: card.findViewById<TextView>(R.id.card_family_text) ?: card.findViewById<TextView>(R.id.card_vip_text)
                val t2 = card.findViewById<TextView>(R.id.card_single_price) ?: card.findViewById<TextView>(R.id.card_family_price) ?: card.findViewById<TextView>(R.id.card_vip_price)
                t1?.setTextColor(textColorDefault)
                t2?.setTextColor(textColorDefault)
            }
            val t1 = selectedCard.findViewById<TextView>(R.id.card_single_text) ?: selectedCard.findViewById<TextView>(R.id.card_family_text) ?: selectedCard.findViewById<TextView>(R.id.card_vip_text)
            val t2 = selectedCard.findViewById<TextView>(R.id.card_single_price) ?: selectedCard.findViewById<TextView>(R.id.card_family_price) ?: selectedCard.findViewById<TextView>(R.id.card_vip_price)
            t1?.setTextColor(textColorSelected)
            t2?.setTextColor(textColorSelected)
        }
        
        cards.forEach { card ->
            val t1 = card.findViewById<TextView>(R.id.card_single_text) ?: card.findViewById<TextView>(R.id.card_family_text) ?: card.findViewById<TextView>(R.id.card_vip_text)
            val t2 = card.findViewById<TextView>(R.id.card_single_price) ?: card.findViewById<TextView>(R.id.card_family_price) ?: card.findViewById<TextView>(R.id.card_vip_price)
            t1?.setTextColor(textColorDefault)
            t2?.setTextColor(textColorDefault)
        }
        setCardTextColor(cardSingle)
    }

    private fun getSelectedDate(datePicker: DatePicker): String {
        val day = datePicker.dayOfMonth
        val month = datePicker.month + 1
        val year = datePicker.year
        return String.format("%02d/%02d/%04d", day, month, year)
    }

    private fun getTicketImage(type: String?): String {
        return when (type) {
            "Single" -> "ic_single_ticket"
            "Family" -> "ic_family"
            "VIP" -> "ic_vip"
            else -> "ic_single_ticket"
        }
    }
} 
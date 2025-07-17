package com.example.museoartiglieriaapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.museoartiglieriaapp.Models.StorageItem
import com.example.museoartiglieriaapp.R
import android.speech.tts.TextToSpeech
import java.util.Locale
import com.bumptech.glide.Glide

class StorageItemAdapter(
    private val items: List<StorageItem>,
    private val onItemClick: (StorageItem) -> Unit
) : RecyclerView.Adapter<StorageItemAdapter.ItemViewHolder>() {

    private var tts: TextToSpeech? = null

    // Funzione di normalizzazione robusta
    private fun normalizeName(name: String): String {
        return name.trim()
            .replace(Regex("[\\s\\-_/]+"), "_")
            .lowercase()
            .replace(Regex("[^a-z0-9_]"), "")
    }

    fun setTTS(tts: TextToSpeech) {
        this.tts = tts
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.item_card_image)
        val nameView: TextView = itemView.findViewById(R.id.item_card_name)
        val originView: TextView = itemView.findViewById(R.id.item_card_origin)
        val yearView: TextView = itemView.findViewById(R.id.item_card_year)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_storage_card, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        
        // Mappa completa nome artefatto -> URL (tutti i 24 link con varianti dei nomi)
        val artifactImageUrls = mapOf(
            // Tanks - con varianti dei nomi
            "PANZER IV" to "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiQXVrDDkJ4PLTZK9F28lYTsRY8uFxA-Lq4TRXBGVMthRDsNLcQ_VykVgN-7fkA2ofhY7IP0Xe4TH3m0D9mdSPEZ45gs1xfxbd5gsejBhPqScVHcOSaYIcJUhmEzxLSawFVg3ifZiLLkB9I/s1600/0+0+0+42100076342_f653e3d6da_b.jpg",
            "Panzer IV" to "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiQXVrDDkJ4PLTZK9F28lYTsRY8uFxA-Lq4TRXBGVMthRDsNLcQ_VykVgN-7fkA2ofhY7IP0Xe4TH3m0D9mdSPEZ45gs1xfxbd5gsejBhPqScVHcOSaYIcJUhmEzxLSawFVg3ifZiLLkB9I/s1600/0+0+0+42100076342_f653e3d6da_b.jpg",
            "TIGER I" to "https://upload.wikimedia.org/wikipedia/commons/6/6e/Bundesarchiv_Bild_101I-299-1805-16%2C_Nordfrankreich%2C_Panzer_VI_%28Tiger_I%29_cropped.jpg",
            "Tiger I" to "https://upload.wikimedia.org/wikipedia/commons/6/6e/Bundesarchiv_Bild_101I-299-1805-16%2C_Nordfrankreich%2C_Panzer_VI_%28Tiger_I%29_cropped.jpg",
            "IS-2" to "https://preview.redd.it/future-tanks-v0-8lkc7e051qia1.jpg?width=320&crop=smart&auto=webp&s=fcb17cbc659034c89317dd2715802e034526c908",
            "MARK I" to "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiENouirmJo3DeyiHP4cSOU_EyrzMMoBzGXgl-IuOdup8Ih1Kxo34pgzDd7qaoWxBQucWXil1mPN-TJiO1RUv880xB-0EIvWVmWu1gXBWMKI7PbfKU6aQxa1W4nvaWxwfckU1mTlYDLnJSg/s1600/carro+armato+con+scritta+in+russo.jpg",
            "Mark I" to "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiENouirmJo3DeyiHP4cSOU_EyrzMMoBzGXgl-IuOdup8Ih1Kxo34pgzDd7qaoWxBQucWXil1mPN-TJiO1RUv880xB-0EIvWVmWu1gXBWMKI7PbfKU6aQxa1W4nvaWxwfckU1mTlYDLnJSg/s1600/carro+armato+con+scritta+in+russo.jpg",
            "M13/40" to "https://www.lasecondaguerramondiale.org/wp-content/uploads/2019/08/91c14cbfa735c2522cedce0eda8810b9.jpg",
            "T-34/85" to "https://eu-wotp.wgcdn.co/dcont/fb/image/t-34-85-austrian-army-museum.jpg",
            // Firearms - con varianti dei nomi
            "M1934" to "https://www.dorotheum.com/fileadmin/lot-images/39W170225/normal/pistole-beretta-mod-1935-kal-7-65-mm-1171429.jpg",
            "M91" to "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcSgFMrKsLjcqgaN9RzKxYM30ChJf2DdW4-_FTADBx4-mlYk3yen",
            "M91/30" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQzgLARVmfP3ul6Q_WhDF2uwoUeSYLAb3rpZ-iikVOl9_-orPWbS3MZ9C2rr4LH_vqXH-Y&usqp=CAU",
            "M1A1" to "https://www.denix.es/it/catalogo/7075/p/denix-Submachine-M1928A1--USA-1918.jpg",
            "STEN GUN" to "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcTAiQDcaypEr9__obXyUxSZmS3g_kptqVsckSqwROjJ4AvMbcLc",
            "MP40" to "https://www.denix.es/it/catalogo/7170/g/denix-Mitragliatrice-MP40--Germania-1940.jpg",
            // Military Equipment - con varianti dei nomi
            "HELMET" to "https://www.arsvalue.com/Upl/Auctions/968/1912/224081/1009-0.jpg",
            "M33" to "https://www.militariainroma.com/wp-content/uploads/2025/04/IMG_9350.jpeg",
            "SS COAT" to "https://www.militariainroma.com/wp-content/uploads/2025/05/IMG_1269-removebg.png",
            "SS Coat" to "https://www.militariainroma.com/wp-content/uploads/2025/05/IMG_1269-removebg.png",
            "M1" to "https://shorturl.at/froIZ",
            "BRODIE" to "https://i.ebayimg.com/images/g/6nQAAOSwmtVljuRx/s-l400.png",
            "USHANKA" to "https://ae01.alicdn.com/kf/Hc7de9fd490c64f35aa8e20756c7684a1a.jpg",
            "Ushanka" to "https://ae01.alicdn.com/kf/Hc7de9fd490c64f35aa8e20756c7684a1a.jpg",
            // Edged Weapons - con varianti dei nomi
            "LEBEL" to "https://i0.wp.com/www.ttmilitaria.com/wp-content/uploads/2024/10/BAYO647-2.jpg?fit=1280%2C853&ssl=1",
            "Lebel" to "https://i0.wp.com/www.ttmilitaria.com/wp-content/uploads/2024/10/BAYO647-2.jpg?fit=1280%2C853&ssl=1",
            "SEITEN" to "https://www.kubel1943.it/foto/pompire.jpg",
            "Seitengewehr" to "https://www.kubel1943.it/foto/pompire.jpg",
            "1907" to "https://images.auctionet.com/thumbs/large_item_3444490_56db6c2d5f.jpg",
            "M1891" to "https://argocoins.com/wp-content/uploads/2023/11/10_04d-019-scaled.jpg",
            "M3" to "https://www.asmc.com/cdn/shop/files/39682-0.jpg?v=1719439619&width=1920",
            "NKM" to "https://aboutww2militaria.com/image/cache/data/Nov15/nr40-combat-knife-scout-reconnaissance-zik-1942-600x600.JPG",
            "Nkm" to "https://aboutww2militaria.com/image/cache/data/Nov15/nr40-combat-knife-scout-reconnaissance-zik-1942-600x600.JPG"
        )
        
        // Provo prima con il nome esatto, poi con la normalizzazione
        var imageUrl = artifactImageUrls[item.name]
        if (imageUrl == null) {
            // Se non trova il nome esatto, prova con la normalizzazione
            val normalizedImageUrls = artifactImageUrls.mapKeys { normalizeName(it.key) }
            val normalizedName = normalizeName(item.name)
            imageUrl = normalizedImageUrls[normalizedName]
        }
        
        if (imageUrl != null && imageUrl.isNotEmpty()) {
            Glide.with(holder.imageView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_shape)
                .error(R.drawable.placeholder_shape)
                .into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_shape)
        }
        holder.nameView.text = item.name
        holder.originView.text = item.origin
        holder.yearView.text = item.yearOfProduction
        
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
} 
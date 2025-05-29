package com.luthfiana.laundry.adapter

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luthfiana.laundry.modeldata.ModelLayanan
import android.view.LayoutInflater
import com.luthfiana.laundry.R
import android.widget.TextView
import androidx.cardview.widget.CardView
import android.view.View
import com.google.firebase.database.DatabaseReference
import com.luthfiana.laundry.layanan.TambahLayanan

class DataLayananAdapter (
    private val listLayanan: ArrayList<ModelLayanan>
) : RecyclerView.Adapter<DataLayananAdapter.ViewHolder>() {
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_layanan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataLayananAdapter.ViewHolder, position: Int) {
        val layanan = listLayanan[position]
        holder.tvIdLayanan.text = layanan.idLayanan
        holder.tvNamaLayanan.text = layanan.namaLayanan
        holder.tvHargaLayanan.text = "Harga: Rp. ${layanan.hargaLayanan}"
        holder.tvCabangLayanan.text = "Cabang: ${layanan.namaCabang}"


        holder.cvCARD_LAYANAN.setOnClickListener {
            val intent = Intent(appContext, TambahLayanan::class.java)
            intent.putExtra("judul", "Edit Layanan")
            intent.putExtra("idLayanan", layanan.idLayanan)
            intent.putExtra("namaLayanan", layanan.namaLayanan)
            intent.putExtra("namaCabang", layanan.namaCabang)
            intent.putExtra("hargaLayanan", layanan.hargaLayanan)
            appContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listLayanan.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdLayanan: TextView = itemView.findViewById(R.id.tvIdLayanan)
        val tvNamaLayanan: TextView = itemView.findViewById(R.id.tvNamaLayanan)
        val tvCabangLayanan: TextView = itemView.findViewById(R.id.tvCabangLayanan)
        val tvHargaLayanan: TextView = itemView.findViewById(R.id.tvHargaLayanan)
        val cvCARD_LAYANAN: CardView = itemView.findViewById(R.id.cvCARD_LAYANAN)
    }
}
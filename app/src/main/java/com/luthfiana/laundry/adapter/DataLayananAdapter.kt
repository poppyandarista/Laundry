package com.luthfiana.laundry.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luthfiana.laundry.modeldata.ModelLayanan
import android.view.LayoutInflater
import com.luthfiana.laundry.R
import android.widget.TextView
import androidx.cardview.widget.CardView
import android.view.View

class DataLayananAdapter (
    private val listLayanan: ArrayList<ModelLayanan>
) : RecyclerView.Adapter<DataLayananAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_layanan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataLayananAdapter.ViewHolder, position: Int) {
        val layanan = listLayanan[position]
        holder.tvIdLayanan.text = layanan.idLayanan
        holder.tvNamaLayanan.text = layanan.namaLayanan
        holder.tvCabangLayanan.text = layanan.namaCabang
        holder.tvHargaLayanan.text = layanan.hargaLayanan

        holder.cvCARD_LAYANAN.setOnClickListener {
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
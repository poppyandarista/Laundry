package com.luthfiana.laundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.luthfiana.laundry.R
import com.luthfiana.laundry.modeldata.ModelPelanggan

class DataPelangganAdapter(
    private val listPelanggan: ArrayList<ModelPelanggan>
) : RecyclerView.Adapter<DataPelangganAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pelanggan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pelanggan = listPelanggan[position]
        holder.tvAlamat.text = pelanggan.alamatPelanggan
        holder.tvIdPelanggan.text = pelanggan.idPelanggan
        holder.tvNamaPelanggan.text = pelanggan.namaPelanggan
        holder.tvNoHpPelanggan.text = pelanggan.nohpPelanggan
        holder.tvTerdaftar.text = pelanggan.terdaftar

        holder.cvCARD_PELANGGAN.setOnClickListener {

        }
        holder.bHubungi.setOnClickListener {
        }
        holder.bLihat.setOnClickListener {
        }
        }

    override fun getItemCount(): Int = listPelanggan.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdPelanggan: TextView = itemView.findViewById(R.id.tvIdPelanggan)
        val tvNamaPelanggan: TextView = itemView.findViewById(R.id.tvNamaPelanggan)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvAlamat)
        val tvNoHpPelanggan: TextView = itemView.findViewById(R.id.tvNoHpPelanggan)
        val tvTerdaftar: TextView = itemView.findViewById(R.id.tvTerdaftar)
        val cvCARD_PELANGGAN: CardView = itemView.findViewById(R.id.cvCARD_PELANGGAN)
        val bHubungi: Button = itemView.findViewById(R.id.bHubungi)
        val bLihat: Button = itemView.findViewById(R.id.bLihat)
    }
}
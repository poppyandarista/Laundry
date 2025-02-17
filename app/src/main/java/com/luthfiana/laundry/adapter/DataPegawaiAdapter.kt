package com.luthfiana.laundry.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luthfiana.laundry.modeldata.ModelPegawai
import android.view.LayoutInflater
import com.luthfiana.laundry.R
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import android.view.View

class DataPegawaiAdapter(
    private val listPegawai: ArrayList<ModelPegawai>
) : RecyclerView.Adapter<DataPegawaiAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pegawai, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataPegawaiAdapter.ViewHolder, position: Int) {
        val pegawai = listPegawai[position]
        holder.tvAlamat.text = pegawai.alamatPegawai
        holder.tvIdPegawai.text = pegawai.idPegawai
        holder.tvNamaPegawai.text = pegawai.namaPegawai
        holder.tvNoHpPegawai.text = pegawai.nohpPegawai
        holder.tvTerdaftar.text = pegawai.terdaftar
        holder.tvCabang.text = pegawai.idCabang

        holder.cvCARD_PEGAWAI.setOnClickListener {

        }
        holder.bHubungi.setOnClickListener {
        }
        holder.bLihat.setOnClickListener {
        }
    }

    override fun getItemCount(): Int = listPegawai.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdPegawai: TextView = itemView.findViewById(R.id.tvIdPegawai)
        val tvNamaPegawai: TextView = itemView.findViewById(R.id.tvNamaPegawai)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvAlamatPegawai)
        val tvNoHpPegawai: TextView = itemView.findViewById(R.id.tvNoHpPegawai)
        val tvTerdaftar: TextView = itemView.findViewById(R.id.tvTerdaftar_pegawai)
        val tvCabang: TextView = itemView.findViewById(R.id.tvCabang_pegawai)
        val cvCARD_PEGAWAI: CardView = itemView.findViewById(R.id.cvCARD_PEGAWAI)
        val bHubungi: Button = itemView.findViewById(R.id.bHubungi_pegawai)
        val bLihat: Button = itemView.findViewById(R.id.bLihat_pegawai)
    }
}
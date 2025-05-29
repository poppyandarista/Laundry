package com.luthfiana.laundry.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.luthfiana.laundry.R
import com.luthfiana.laundry.modeldata.ModelPelanggan
import com.luthfiana.laundry.transaksi.transaksi

class PilihPelangganAdapter(
    private val listPelanggan: ArrayList<ModelPelanggan>
) : RecyclerView.Adapter<PilihPelangganAdapter.ViewHolder>() {

    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdPelanggan: TextView = itemView.findViewById(R.id.tvNoUrutan)
        val tvNamaPelanggan: TextView = itemView.findViewById(R.id.tvNamaPelanggan)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvAlamat)
        val tvNoHpPelanggan: TextView = itemView.findViewById(R.id.tvNoHpPelanggan)
        val cvCardPelanggan: CardView = itemView.findViewById(R.id.cvCARD_PILIH_PELANGGAN)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        appContext = parent.context
        val view = LayoutInflater.from(appContext).inflate(R.layout.card_pilih_pelanggan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomor = listPelanggan.size - position
        val item = listPelanggan[position]

        holder.tvIdPelanggan.text = "[$nomor]"
        holder.tvNamaPelanggan.text = item.namaPelanggan
        holder.tvAlamat.text = "Alamat: ${item.alamatPelanggan}"
        holder.tvNoHpPelanggan.text = "No HP: ${item.nohpPelanggan}"

        holder.cvCardPelanggan.setOnClickListener {
            val intent = Intent()
            intent.putExtra("idPelanggan", item.idPelanggan)
            intent.putExtra("namaPelanggan", item.namaPelanggan)
            intent.putExtra("nohpPelanggan", item.nohpPelanggan)
            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }
    }

    override fun getItemCount(): Int = listPelanggan.size
}

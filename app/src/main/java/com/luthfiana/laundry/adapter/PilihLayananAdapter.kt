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
import com.luthfiana.laundry.modeldata.ModelLayanan
import com.luthfiana.laundry.transaksi.transaksi

class PilihLayananAdapter(
    private val listLayanan: ArrayList<ModelLayanan>
) : RecyclerView.Adapter<PilihLayananAdapter.ViewHolder>() {

    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdLayanan: TextView = itemView.findViewById(R.id.tvNoUrutanLayanan)
        val tvNamaLayanan: TextView = itemView.findViewById(R.id.tvNamaLayanan)
        val tvHargaLayanan: TextView = itemView.findViewById(R.id.tvHargaLayanan)
        val cvCardLayanan: CardView = itemView.findViewById(R.id.cvCARD_PILIH_LAYANAN)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        appContext = parent.context
        val view = LayoutInflater.from(appContext).inflate(R.layout.card_pilih_layanan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomor = listLayanan.size - position
        val item = listLayanan[position]

        holder.tvIdLayanan.text = "[$nomor]"
        holder.tvNamaLayanan.text = item.namaLayanan
        holder.tvHargaLayanan.text = appContext.getString(R.string.tvHarga_titikdua) + " " + item.namaCabang

        holder.cvCardLayanan.setOnClickListener {
            val intent = Intent()
            intent.putExtra("idLayanan", item.idLayanan)
            intent.putExtra("namaLayanan", item.namaLayanan)
            intent.putExtra("hargaLayanan", item.namaCabang)
            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }
    }

    override fun getItemCount(): Int = listLayanan.size
}

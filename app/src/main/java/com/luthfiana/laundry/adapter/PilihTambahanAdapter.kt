package com.luthfiana.laundry.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.luthfiana.laundry.R
import com.luthfiana.laundry.modeldata.ModelTambahan
import com.luthfiana.laundry.transaksi.KonfirmasiTransaksi
import com.luthfiana.laundry.transaksi.transaksi

class PilihTambahanAdapter(
    private val listTambahan: ArrayList<ModelTambahan>
) : RecyclerView.Adapter<PilihTambahanAdapter.ViewHolder>() {

    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference
    private var onItemClickListener: ((ModelTambahan, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (ModelTambahan, Int) -> Unit) {
        this.onItemClickListener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdTambahan: TextView = itemView.findViewById(R.id.tvNoUrutanTambahan)
        val tvNamaTambahan: TextView = itemView.findViewById(R.id.tvNamaLayananTambahan)
        val tvHargaTambahan: TextView = itemView.findViewById(R.id.tvHargaTambahan)
        val cvCardTambahan: CardView = itemView.findViewById(R.id.cvCARD_PILIH_TAMBAHAN)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        appContext = parent.context
        val view = LayoutInflater.from(appContext).inflate(R.layout.card_pilih_tambahan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomor = if (appContext is transaksi || appContext is KonfirmasiTransaksi) {
            position + 1
        } else {
            listTambahan.size - position
        }

        val item = listTambahan[position]
        holder.tvIdTambahan.text = "[$nomor]"
        holder.tvNamaTambahan.text = item.namaLayananTambahan
        holder.tvHargaTambahan.text = appContext.getString(R.string.tvHarga_titikdua) + " " + item.hargaTambahan

        // Tampilkan icon delete hanya di Transaksi Activity
        if (appContext is transaksi) {
            holder.ivDelete.visibility = View.VISIBLE
            holder.ivDelete.setOnClickListener {
                onItemClickListener?.invoke(item, position)
            }
        } else {
            holder.ivDelete.visibility = View.GONE
        }

        holder.cvCardTambahan.setOnClickListener {
            Log.d("PilihTambahanAdapter", "Item diklik: ${item.namaLayananTambahan}")

            if (!item.idTambahan.isNullOrEmpty() && !item.namaLayananTambahan.isNullOrEmpty()
                && !(appContext is transaksi || appContext is KonfirmasiTransaksi)) {
                val intent = Intent()
                intent.putExtra("idTambahan", item.idTambahan)
                intent.putExtra("namaLayananTambahan", item.namaLayananTambahan)
                intent.putExtra("hargaTambahan", item.hargaTambahan)
                (appContext as Activity).setResult(Activity.RESULT_OK, intent)
                (appContext as Activity).finish()
            }
        }
    }

    override fun getItemCount(): Int = listTambahan.size

    fun updateData(newList: List<ModelTambahan>) {
        listTambahan.clear()
        listTambahan.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        listTambahan.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listTambahan.size)
    }
}
package com.luthfiana.laundry.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luthfiana.laundry.modeldata.ModelTambahan
import android.view.LayoutInflater
import com.luthfiana.laundry.R
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.luthfiana.laundry.tambahan.TambahTambahan
class DataTambahanAdapter(
    private val listTambahan: ArrayList<ModelTambahan>
) : RecyclerView.Adapter<DataTambahanAdapter.ViewHolder>() {

    lateinit var appContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_tambahan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tambahan = listTambahan[position]

        holder.tvIdTambahan.text = tambahan.idTambahan
        holder.tvNamaLayananTambahan.text = tambahan.namaLayananTambahan
        holder.tvHarga.text = "Harga: ${tambahan.hargaTambahan}"
        holder.tvNamaCabang.text = "Cabang: ${tambahan.namaCabang}"
        holder.tvStatus.text = "Status: ${tambahan.status}"

        holder.cvCARD_TAMBAHAN.setOnClickListener {
            val intent = Intent(appContext, TambahTambahan::class.java)
            intent.putExtra("judul", "Edit Layanan Tambahan")
            intent.putExtra("idTambahan", tambahan.idTambahan)
            intent.putExtra("namaLayananTambahan", tambahan.namaLayananTambahan)
            intent.putExtra("hargaTambahan", tambahan.hargaTambahan)
            intent.putExtra("namaCabang", tambahan.namaCabang)
            intent.putExtra("status", tambahan.status)
            appContext.startActivity(intent)
        }

        holder.bEdit.setOnClickListener {
            val intent = Intent(appContext, TambahTambahan::class.java) // FIX: bukan TambahCabang
            intent.putExtra("judul", "Edit Tambahan")
            intent.putExtra("idTambahan", tambahan.idTambahan)
            intent.putExtra("namaLayananTambahan", tambahan.namaLayananTambahan)
            intent.putExtra("hargaTambahan", tambahan.hargaTambahan)
            intent.putExtra("namaCabang", tambahan.namaCabang)
            intent.putExtra("status", tambahan.status)
            appContext.startActivity(intent)
        }

        holder.bLihat.setOnClickListener {
            showDialog(holder, tambahan, position)
        }
    }

    override fun getItemCount(): Int = listTambahan.size

    fun setData(newList: List<ModelTambahan>) {
        listTambahan.clear()
        listTambahan.addAll(newList)
        notifyDataSetChanged()
    }

    private fun showDialog(holder: ViewHolder, tambahan: ModelTambahan, position: Int) {
        val dialogView = LayoutInflater.from(appContext).inflate(R.layout.dialog_mod_tambahan, null)

        val tvId = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_TAMBAHAN_ISI_Id)
        val tvNama = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_TAMBAHAN_ISI_NamaLayananTambahan)
        val tvHarga = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_TAMBAHAN_ISI_Harga)
        val tvNamaCabang = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_TAMBAHAN_ISI_NamaCabang)
        val tvStatus = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_TAMBAHAN_ISI_Status)
        val btSunting = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_TAMBAHAN_Sunting)
        val btHapus = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_TAMBAHAN_Hapus)

        tvId.text = tambahan.idTambahan
        tvNama.text = tambahan.namaLayananTambahan
        tvHarga.text = tambahan.hargaTambahan
        tvNamaCabang.text = tambahan.namaCabang
        tvStatus.text = tambahan.status

        val dialog = AlertDialog.Builder(appContext)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        dialog.show()

        btSunting.setOnClickListener {
            val intent = Intent(appContext, TambahTambahan::class.java)
            intent.putExtra("judul", "Edit Tambahan")
            intent.putExtra("idTambahan", tambahan.idTambahan)
            intent.putExtra("namaLayananTambahan", tambahan.namaLayananTambahan)
            intent.putExtra("hargaTambahan", tambahan.hargaTambahan)
            intent.putExtra("namaCabang", tambahan.namaCabang)
            intent.putExtra("status", tambahan.status)
            appContext.startActivity(intent)
            dialog.dismiss()
        }

        btHapus.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Konfirmasi")
                .setMessage("Apakah Anda yakin ingin menghapus layanan tambahan ini?")
                .setPositiveButton("Hapus") { _, _ ->
                    val idTambahan = tambahan.idTambahan ?: return@setPositiveButton
                    val ref = FirebaseDatabase.getInstance().getReference("tambahan")

                    ref.child(idTambahan).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(holder.itemView.context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                            if (position in 0 until listTambahan.size) {
                                listTambahan.removeAt(position)
                                notifyItemRemoved(position)
                            }
                            dialog.dismiss()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(holder.itemView.context, "Gagal menghapus: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdTambahan: TextView = itemView.findViewById(R.id.tvIdTambahan)
        val tvNamaLayananTambahan: TextView = itemView.findViewById(R.id.tvNamaLayananTambahan)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHarga)
        val tvNamaCabang: TextView = itemView.findViewById(R.id.tvNamaCabang)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val cvCARD_TAMBAHAN: CardView = itemView.findViewById(R.id.cvCARD_TAMBAHAN)
        val bEdit: Button = itemView.findViewById(R.id.bEdit)
        val bLihat: Button = itemView.findViewById(R.id.bLihat)
    }
}

package com.luthfiana.laundry.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luthfiana.laundry.modeldata.ModelPegawai
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
import com.luthfiana.laundry.pegawai.tambah_pegawai

class DataPegawaiAdapter(
    private val listPegawai: ArrayList<ModelPegawai>
) : RecyclerView.Adapter<DataPegawaiAdapter.ViewHolder>(){

    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pegawai, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataPegawaiAdapter.ViewHolder, position: Int) {
        val pegawai = listPegawai[position]
        databaseReference = FirebaseDatabase.getInstance().getReference("Pegawai")

        holder.tvAlamat.text = "Alamat: ${pegawai.alamatPegawai}"
        holder.tvIdPegawai.text = pegawai.idPegawai
        holder.tvNamaPegawai.text = pegawai.namaPegawai
        holder.tvNoHpPegawai.text = "No HP: ${pegawai.nohpPegawai}"
        holder.tvTerdaftar.text = "Terdaftar: ${pegawai.terdaftar}"
        holder.tvCabang.text = "Cabang: ${pegawai.idCabang}"

        holder.cvCARD_PEGAWAI.setOnClickListener {
            val intent = Intent(appContext, tambah_pegawai::class.java)
            intent.putExtra("judul", "Edit Pegawai")
            intent.putExtra("idPegawai", pegawai.idPegawai)
            intent.putExtra("namaPegawai", pegawai.namaPegawai)
            intent.putExtra("nohpPegawai", pegawai.nohpPegawai)
            intent.putExtra("alamatPegawai", pegawai.alamatPegawai)
            intent.putExtra("idCabang", pegawai.idCabang)
            appContext.startActivity(intent)
        }
        holder.bHubungi.setOnClickListener {
            val localNumber = pegawai.nohpPegawai ?: ""
            val cleanedNumber = localNumber
                .replace("\\s".toRegex(), "")
                .replace("-", "")
                .replace("[^\\d]".toRegex(), "")

            val internationalNumber = when {
                cleanedNumber.startsWith("0") -> "62" + cleanedNumber.substring(1)
                cleanedNumber.startsWith("62") -> cleanedNumber
                else -> "62$cleanedNumber"
            }

            val pesan = "Halo ${pegawai.namaPegawai}, saya ingin bertanya tentang layanan laundry ya."
            val url = "https://wa.me/$internationalNumber?text=${Uri.encode(pesan)}"

            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                appContext.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(appContext, "Gagal membuka WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        holder.bLihat.setOnClickListener {
            val dialogView = LayoutInflater.from(appContext).inflate(R.layout.dialog_mod_pegawai, null)

            val tvId = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_PEGAWAI_ISI_Id)
            val tvNama = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_PEGAWAI_ISI_Nama)
            val tvCabang = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_PEGAWAI_ISI_Cabang)
            val tvAlamat = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_PEGAWAI_ISI_Alamat)
            val tvNoHp = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_PEGAWAI_ISI_Nohp)
            val tvTerdaftar = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_PEGAWAI_ISI_Terdaftar)
            val btSunting = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_PEGAWAI_Sunting)
            val btHapus = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_PEGAWAI_Hapus)

            tvId.text = pegawai.idPegawai
            tvNama.text = pegawai.namaPegawai
            tvCabang.text = pegawai.idCabang
            tvAlamat.text = pegawai.alamatPegawai
            tvNoHp.text = pegawai.nohpPegawai
            tvTerdaftar.text = pegawai.terdaftar

            val dialog = AlertDialog.Builder(appContext)
                .setView(dialogView)
                .setCancelable(true)
                .create()
            dialog.show()

            btSunting.setOnClickListener {
                val intent = Intent(appContext, tambah_pegawai::class.java)
                intent.putExtra("judul", "Edit Pegawai")
                intent.putExtra("idPegawai", pegawai.idPegawai)
                intent.putExtra("namaPegawai", pegawai.namaPegawai)
                intent.putExtra("idCabang", pegawai.idCabang)
                intent.putExtra("alamatPegawai", pegawai.alamatPegawai)
                intent.putExtra("nohpPegawai", pegawai.nohpPegawai)
                appContext.startActivity(intent)
                dialog.dismiss()
            }

            btHapus.setOnClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah Anda yakin ingin menghapus pelanggan ini?")
                    .setPositiveButton("Hapus") { _, _ ->
                        val idPegawai = pegawai.idPegawai
                        if (idPegawai.isNullOrEmpty()) {
                            Toast.makeText(holder.itemView.context, "ID Pegawai tidak valid!", Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }

                        databaseReference = FirebaseDatabase.getInstance().getReference("pegawai")
                        databaseReference.child(idPegawai).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(holder.itemView.context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                                if (position != RecyclerView.NO_POSITION && position < listPegawai.size) {
                                    listPegawai.removeAt(position)
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
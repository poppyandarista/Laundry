package com.luthfiana.laundry.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luthfiana.laundry.modeldata.ModelPelanggan
import android.view.LayoutInflater
import com.luthfiana.laundry.R
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.luthfiana.laundry.pelanggan.TambahPelanggan

class DataPelangganAdapter(
    private val listPelanggan: ArrayList<ModelPelanggan>,
) : RecyclerView.Adapter<DataPelangganAdapter.ViewHolder>() {

    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pelanggan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pelanggan = listPelanggan[position]
        databaseReference = FirebaseDatabase.getInstance().getReference("pelanggan")

        holder.tvIdPelanggan.text = pelanggan.idPelanggan
        holder.tvNamaPelanggan.text = pelanggan.namaPelanggan
        holder.tvCabangPelanggan.text = appContext.getString(R.string.card_cabangg, pelanggan.idCabang)
        holder.tvAlamat.text = appContext.getString(R.string.card_alamat, pelanggan.alamatPelanggan)
        holder.tvNoHpPelanggan.text = appContext.getString(R.string.card_nohp, pelanggan.nohpPelanggan)
        holder.tvTerdaftar.text = appContext.getString(R.string.card_terdaftar, pelanggan.terdaftar)

        holder.cvCARD_PELANGGAN.setOnClickListener {
            val intent = Intent(appContext, TambahPelanggan::class.java)
            intent.putExtra("judul", "Edit Pelanggan")
            intent.putExtra("idPelanggan", pelanggan.idPelanggan)
            intent.putExtra("namaPelanggan", pelanggan.namaPelanggan)
            intent.putExtra("idCabang", pelanggan.idCabang)
            intent.putExtra("alamatPelanggan", pelanggan.alamatPelanggan)
            intent.putExtra("nohpPelanggan", pelanggan.nohpPelanggan)
            appContext.startActivity(intent)
        }

        holder.bHubungi.setOnClickListener {
            val localNumber = pelanggan.nohpPelanggan ?: ""
            val cleanedNumber = localNumber
                .replace("\\s".toRegex(), "")
                .replace("-", "")
                .replace("[^\\d]".toRegex(), "")

            val internationalNumber = when {
                cleanedNumber.startsWith("0") -> "62" + cleanedNumber.substring(1)
                cleanedNumber.startsWith("62") -> cleanedNumber
                else -> "62$cleanedNumber"
            }

            val pesan = "Halo ${pelanggan.namaPelanggan}, saya ingin bertanya tentang layanan laundry ya."
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
            val dialogView = LayoutInflater.from(appContext).inflate(R.layout.dialog_mod_pelanggan, null)

            val tvId = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_PELANGGAN_ISI_Id)
            val tvNama = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_PELANGGAN_ISI_Nama)
            val tvCabang = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_PELANGGAN_ISI_Cabang)
            val tvAlamat = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_PELANGGAN_ISI_Alamat)
            val tvNoHp = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_PELANGGAN_ISI_Nohp)
            val tvTerdaftar = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_PELANGGAN_ISI_Terdaftar)
            val btSunting = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_PELANGGAN_Sunting)
            val btHapus = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_PELANGGAN_Hapus)

            tvId.text = pelanggan.idPelanggan
            tvNama.text = pelanggan.namaPelanggan
            tvCabang.text = pelanggan.idCabang
            tvAlamat.text = pelanggan.alamatPelanggan
            tvNoHp.text = pelanggan.nohpPelanggan
            tvTerdaftar.text = pelanggan.terdaftar

            val dialog = AlertDialog.Builder(appContext)
                .setView(dialogView)
                .setCancelable(true)
                .create()
            dialog.show()

            btSunting.setOnClickListener {
                val intent = Intent(appContext, TambahPelanggan::class.java)
                intent.putExtra("judul", "Edit Pelanggan")
                intent.putExtra("idPelanggan", pelanggan.idPelanggan)
                intent.putExtra("namaPelanggan", pelanggan.namaPelanggan)
                intent.putExtra("idCabang", pelanggan.idCabang)
                intent.putExtra("alamatPelanggan", pelanggan.alamatPelanggan)
                intent.putExtra("nohpPelanggan", pelanggan.nohpPelanggan)
                appContext.startActivity(intent)
                dialog.dismiss()
            }

            btHapus.setOnClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle(appContext.getString(R.string.konfirmasi))
                    .setMessage(appContext.getString(R.string.konfirmasihapuspegawai))
                    .setPositiveButton(appContext.getString(R.string.tv_hapus)) { _, _ ->
                    val idPelanggan = pelanggan.idPelanggan
                        if (idPelanggan.isNullOrEmpty()) {
                            Toast.makeText(appContext, appContext.getString(R.string.idtidakvalid), Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }

                        databaseReference = FirebaseDatabase.getInstance().getReference("pelanggan")
                        databaseReference.child(idPelanggan).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(appContext, appContext.getString(R.string.databerhasildihapus), Toast.LENGTH_SHORT).show()
                                val pos = holder.adapterPosition
                                if (pos != RecyclerView.NO_POSITION && pos < listPelanggan.size) {
                                    listPelanggan.removeAt(pos)
                                    notifyItemRemoved(pos)
                            }
                                dialog.dismiss()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(appContext, appContext.getString(R.string.gagalhapus) + ": ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton(appContext.getString(R.string.batal), null)
                    .show()
            }
        }
    }

    override fun getItemCount(): Int = listPelanggan.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdPelanggan: TextView = itemView.findViewById(R.id.tvIdPelanggan)
        val tvNamaPelanggan: TextView = itemView.findViewById(R.id.tvNamaPelanggan)
        val tvCabangPelanggan: TextView = itemView.findViewById(R.id.tvCabangPelanggan)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvAlamat)
        val tvNoHpPelanggan: TextView = itemView.findViewById(R.id.tvNoHpPelanggan)
        val tvTerdaftar: TextView = itemView.findViewById(R.id.tvTerdaftar)
        val cvCARD_PELANGGAN: CardView = itemView.findViewById(R.id.cvCARD_PELANGGAN)
        val bHubungi: Button = itemView.findViewById(R.id.bHubungi)
        val bLihat: Button = itemView.findViewById(R.id.bLihat)
    }
}

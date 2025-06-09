package com.luthfiana.laundry.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luthfiana.laundry.modeldata.ModelCabang
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
import com.luthfiana.laundry.cabang.TambahCabang

class DataCabangAdapter(
    private val listCabang: ArrayList<ModelCabang>
) : RecyclerView.Adapter<DataCabangAdapter.ViewHolder>() {

    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_cabang, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataCabangAdapter.ViewHolder, position: Int) {
        val cabang = listCabang[position]
        databaseReference = FirebaseDatabase.getInstance().getReference("cabang")

        holder.tvIdCabang.text = cabang.idCabang
        holder.tvNamaCabang.text = cabang.namaCabang
        holder.tvAlamatCabang.text = appContext.getString(R.string.card_alamat, cabang.alamatCabang)
        holder.tvNoHpCabang.text = appContext.getString(R.string.card_nohp, cabang.nohpCabang)

        holder.cvCARD_CABANG.setOnClickListener {
            val intent = Intent(appContext, TambahCabang::class.java)
            intent.putExtra("judul", appContext.getString(R.string.editcabang))
            intent.putExtra("idCabang", cabang.idCabang)
            intent.putExtra("namaCabang", cabang.namaCabang)
            intent.putExtra("alamatCabang", cabang.alamatCabang)
            intent.putExtra("nohpCabang", cabang.nohpCabang)
            appContext.startActivity(intent)
        }
        holder.bEdit.setOnClickListener {
            val intent = Intent(appContext, TambahCabang::class.java)
            intent.putExtra("judul", appContext.getString(R.string.editcabang))
            intent.putExtra("idCabang", cabang.idCabang)
            intent.putExtra("namaCabang", cabang.namaCabang)
            intent.putExtra("alamatCabang", cabang.alamatCabang)
            intent.putExtra("nohpCabang", cabang.nohpCabang)
            appContext.startActivity(intent)
        }

        holder.bLihat.setOnClickListener {
            val dialogView = LayoutInflater.from(appContext).inflate(R.layout.dialog_mod_cabang, null)

            val tvId = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_CABANG_ISI_Id)
            val tvNama = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_CABANG_ISI_NamaCabang)
            val tvAlamat = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_CABANG_ISI_Alamat)
            val tvNoHp = dialogView.findViewById<TextView>(R.id.tvDIALOG_MOD_CABANG_ISI_Nohp)
            val btSunting = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_CABANG_Sunting)
            val btHapus = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_CABANG_Hapus)

            tvId.text = cabang.idCabang
            tvNama.text = cabang.namaCabang
            tvAlamat.text = cabang.alamatCabang
            tvNoHp.text = cabang.nohpCabang

            val dialog = AlertDialog.Builder(appContext)
                .setView(dialogView)
                .setCancelable(true)
                .create()
            dialog.show()

            btSunting.setOnClickListener {
                val intent = Intent(appContext, TambahCabang::class.java)
                intent.putExtra("judul", appContext.getString(R.string.editcabang))
                intent.putExtra("idCabang", cabang.idCabang)
                intent.putExtra("namaCabang", cabang.namaCabang)
                intent.putExtra("alamatCabang", cabang.alamatCabang)
                intent.putExtra("nohpCabang", cabang.nohpCabang)
                appContext.startActivity(intent)
                dialog.dismiss()
            }

            btHapus.setOnClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle(appContext.getString(R.string.konfirmasi))
                    .setMessage(appContext.getString(R.string.konfirmasi_hapuscabang))
                    .setPositiveButton(appContext.getString(R.string.tv_hapus)) { _, _ ->
                        val idCabang = cabang.idCabang
                        if (idCabang.isNullOrEmpty()) {
                            Toast.makeText(appContext, appContext.getString(R.string.idtidakvalid), Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }

                        databaseReference = FirebaseDatabase.getInstance().getReference("cabang")
                        databaseReference.child(idCabang).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(appContext, appContext.getString(R.string.databerhasildihapus), Toast.LENGTH_SHORT).show()
                                if (position != RecyclerView.NO_POSITION && position < listCabang.size) {
                                    listCabang.removeAt(position)
                                    notifyItemRemoved(position)
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

    override fun getItemCount(): Int = listCabang.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdCabang: TextView = itemView.findViewById(R.id.tvIdCabang)
        val tvNamaCabang: TextView = itemView.findViewById(R.id.tvNamaCabang)
        val tvAlamatCabang: TextView = itemView.findViewById(R.id.tvAlamatCabang)
        val tvNoHpCabang: TextView = itemView.findViewById(R.id.tvNoHpCabang)
        val cvCARD_CABANG: CardView = itemView.findViewById(R.id.cvCARD_CABANG)
        val bEdit: Button = itemView.findViewById(R.id.bEdit)
        val bLihat: Button = itemView.findViewById(R.id.bLihat)
    }
}

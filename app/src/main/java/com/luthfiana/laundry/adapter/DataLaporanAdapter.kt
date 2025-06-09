package com.luthfiana.laundry.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luthfiana.laundry.R
import com.luthfiana.laundry.modeldata.ModelLaporan
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class DataLaporanAdapter(
    private val context: Context,
    private val laporanList: MutableList<ModelLaporan> = mutableListOf()
) : RecyclerView.Adapter<DataLaporanAdapter.LaporanViewHolder>() {
    init {
        loadDataFromFirebase()
    }

    inner class LaporanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaPelanggan: TextView = itemView.findViewById(R.id.tvNamaPelanggan)
        val tvTanggalOrder: TextView = itemView.findViewById(R.id.tvTanggalOrder)
        val tvStatusPembayaran: TextView = itemView.findViewById(R.id.tvStatusPembayaran)
        val tvHargaTotal: TextView = itemView.findViewById(R.id.tvHargaTotal)
        val btnBayar: Button = itemView.findViewById(R.id.bBayarSekarang)
        val tvLayananUtama: TextView = itemView.findViewById(R.id.tvLayananUtama)
        val tvJumlahTambahan: TextView = itemView.findViewById(R.id.tvJumlahTambahan)
        val tvNoUrut: TextView = itemView.findViewById(R.id.tvNomorAntrian)
        val tvWaktuPengambilan: TextView = itemView.findViewById(R.id.tvWaktuPengambilan)
        val tvDiambilPada: TextView = itemView.findViewById(R.id.tvDiambilPada)
    }

    private fun loadDataFromFirebase() {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("laporan")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                laporanList.clear()
                for (data in snapshot.children) {
                    val laporan = data.getValue(ModelLaporan::class.java)
                    laporan?.let {
                        it.idTransaksi = data.key ?: ""
                        laporanList.add(it)
                    }
                }
                notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_laporan, parent, false)
        return LaporanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LaporanViewHolder, position: Int) {
        val laporan = laporanList[position]

        holder.tvNamaPelanggan.text = laporan.namaPelanggan
        holder.tvTanggalOrder.text = laporan.tanggalTransaksi
        holder.tvHargaTotal.text = formatRupiah(laporan.totalBayar?.toDoubleOrNull() ?: 0.0)
        holder.tvLayananUtama.text = laporan.namaLayanan
        holder.tvNoUrut.text = "[${position + 1}]"

        // Calculate number of additional services
        val tambahanCount = laporan.totalTambahan?.let {
            if (it.isNotEmpty() && it != "0") {
                it.toIntOrNull() ?: 0
            } else {
                0
            }
        } ?: 0

        if (tambahanCount > 0) {
            holder.tvJumlahTambahan.text = context.getString(R.string.tvlayanantambahan, tambahanCount)
            holder.tvJumlahTambahan.visibility = View.VISIBLE
        } else {
            holder.tvJumlahTambahan.text = context.getString(R.string.no_additional_services)
            holder.tvJumlahTambahan.visibility = View.VISIBLE
        }


        // Atur status pembayaran dan tombol
        // Atur status pembayaran dan tombol
        when (laporan.statusPembayaran) {
            "Belum Dibayar" -> {
                holder.tvStatusPembayaran.text = context.getString(R.string.status_unpaid)
                holder.tvStatusPembayaran.setBackgroundResource(R.drawable.bg_status_merah)
                holder.tvStatusPembayaran.setTextColor(ContextCompat.getColor(context, R.color.red))

                holder.btnBayar.visibility = View.VISIBLE
                holder.btnBayar.text = context.getString(R.string.pay_now)
                holder.btnBayar.backgroundTintList = ContextCompat.getColorStateList(context, R.color.red)
                holder.btnBayar.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.btnBayar.setOnClickListener {
                    showPaymentDialog(laporan.idTransaksi, position)
                }
                holder.tvDiambilPada.visibility = View.GONE
                holder.tvWaktuPengambilan.visibility = View.GONE
            }
            "Sudah Dibayar" -> {
                holder.tvStatusPembayaran.text = context.getString(R.string.status_paid)
                holder.tvStatusPembayaran.setBackgroundResource(R.drawable.bg_status_kuning)
                holder.tvStatusPembayaran.setTextColor(ContextCompat.getColor(context, R.color.orange))

                holder.btnBayar.visibility = View.VISIBLE
                holder.btnBayar.text = context.getString(R.string.pickup_now)
                holder.btnBayar.backgroundTintList = ContextCompat.getColorStateList(context, R.color.blue)
                holder.btnBayar.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.btnBayar.setOnClickListener {
                    updateOrderStatus(laporan.idTransaksi, position)
                }
                holder.tvDiambilPada.visibility = View.GONE
                holder.tvWaktuPengambilan.visibility = View.GONE
            }
            "Selesai" -> {
                holder.tvStatusPembayaran.text = context.getString(R.string.status_done)
                holder.tvStatusPembayaran.setBackgroundResource(R.drawable.bg_status_hijau)
                holder.tvStatusPembayaran.setTextColor(ContextCompat.getColor(context, R.color.green))
                holder.btnBayar.visibility = View.GONE

                // Tampilkan waktu pengambilan HANYA jika status Selesai
                holder.tvDiambilPada.visibility = View.VISIBLE
                holder.tvWaktuPengambilan.visibility = View.VISIBLE

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val waktuPengambilan = laporan.waktuPengambilan ?: sdf.format(Date())
                holder.tvWaktuPengambilan.text = waktuPengambilan
            }
        }
    }


    private fun showPaymentDialog(idTransaksi: String, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_mod_pembayaran, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Inisialisasi tombol-tombol pembayaran
        val bBayarNanti = dialogView.findViewById<Button>(R.id.bBayarNanti)
        val bTunai = dialogView.findViewById<Button>(R.id.bTunai)
        val bQRIS = dialogView.findViewById<Button>(R.id.bQRIS)
        val bDana = dialogView.findViewById<Button>(R.id.bDana)
        val bGopay = dialogView.findViewById<Button>(R.id.bGopay)
        val bOVO = dialogView.findViewById<Button>(R.id.bOVO)
        val tvBatal = dialogView.findViewById<TextView>(R.id.tvBatal)
        tvBatal.setOnClickListener {
            dialog.dismiss()
        }

        bBayarNanti.setOnClickListener {
            updatePaymentStatus(idTransaksi, position, "Bayar Nanti")
            dialog.dismiss()
        }

        bTunai.setOnClickListener {
            updatePaymentStatus(idTransaksi, position, "Tunai")
            dialog.dismiss()
        }

        bQRIS.setOnClickListener {
            updatePaymentStatus(idTransaksi, position, "QRIS")
            dialog.dismiss()
        }

        bDana.setOnClickListener {
            updatePaymentStatus(idTransaksi, position, "DANA")
            dialog.dismiss()
        }

        bGopay.setOnClickListener {
            updatePaymentStatus(idTransaksi, position, "GoPay")
            dialog.dismiss()
        }

        bOVO.setOnClickListener {
            updatePaymentStatus(idTransaksi, position, "OVO")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updatePaymentStatus(idTransaksi: String, position: Int, method: String) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("laporan").child(idTransaksi)

        val status = if (method == "Bayar Nanti") "Belum Dibayar" else "Sudah Dibayar"

        val updates = hashMapOf<String, Any>(
            "statusPembayaran" to status,
            "metodePembayaran" to method
        )

        reference.updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(context, context.getString(R.string.pembayaranberhasil_toast), Toast.LENGTH_SHORT).show()
                notifyItemChanged(position)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, context.getString(R.string.gagalmemperbaruistatus_toast), Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrderStatus(idTransaksi: String, position: Int) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("laporan").child(idTransaksi)

        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val updates = hashMapOf<String, Any>(
            "statusPembayaran" to "Selesai",
            "waktuPengambilan" to currentTime
        )

        reference.updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(context, context.getString(R.string.statusorderdiperbarui_toast), Toast.LENGTH_SHORT).show()
                notifyItemChanged(position)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, context.getString(R.string.gagalmemperbaruistatus_toast), Toast.LENGTH_SHORT).show()

            }
    }

    override fun getItemCount(): Int = laporanList.size

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number)
    }
}
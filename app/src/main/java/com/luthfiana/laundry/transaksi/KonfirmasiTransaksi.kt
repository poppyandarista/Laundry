package com.luthfiana.laundry.transaksi

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.luthfiana.laundry.R
import com.luthfiana.laundry.adapter.PilihTambahanAdapter
import com.luthfiana.laundry.laporan.DataLaporan
import com.luthfiana.laundry.modeldata.ModelTambahan
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KonfirmasiTransaksi : AppCompatActivity() {

    private lateinit var tvNamaPelanggan: TextView
    private lateinit var tvNoHp: TextView
    private lateinit var tvNamaLayanan: TextView
    private lateinit var tvHargaLayanan: TextView
    private lateinit var tvTotalBayar: TextView
    private lateinit var rvKonfirmasiData: RecyclerView
    private lateinit var bBatal: Button
    private lateinit var bPembayaran: Button

    private val tambahanList = mutableListOf<ModelTambahan>()
    private lateinit var adapter: PilihTambahanAdapter

    private var hargaTotal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_konfirmasi_transaksi)

        initViews()
        setupRecyclerView()
        getDataFromIntent()
        calculateTotal()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupButtonListeners()
    }

    private fun initViews() {
        tvNamaPelanggan = findViewById(R.id.namapelanggan_konfirmasi)
        tvNoHp = findViewById(R.id.nohp_konfirmasi)
        tvNamaLayanan = findViewById(R.id.namalayanan_konfirmasi)
        tvHargaLayanan = findViewById(R.id.harga_konfirmasi)
        tvTotalBayar = findViewById(R.id.tv_totalbayar_konfirmasi)
        rvKonfirmasiData = findViewById(R.id.rvKonfirmasiData)
        bBatal = findViewById(R.id.bBatal)
        bPembayaran = findViewById(R.id.bPembayaran)
    }

    private fun setupRecyclerView() {
        adapter = PilihTambahanAdapter(ArrayList(tambahanList))
        rvKonfirmasiData.layoutManager = LinearLayoutManager(this)
        rvKonfirmasiData.adapter = adapter
    }
    private fun getDataFromIntent() {
        try {
            intent?.let {
                // Get customer data
                tvNamaPelanggan.text = it.getStringExtra("namaPelanggan") ?: "-"
                tvNoHp.text = it.getStringExtra("nohpPelanggan") ?: "-"

                // Get main service data
                tvNamaLayanan.text = it.getStringExtra("namaLayanan") ?: "-"
                val harga = it.getStringExtra("hargaLayanan")?.toIntOrNull() ?: 0
                tvHargaLayanan.text = formatRupiah(harga.toDouble())
                hargaTotal = harga

                // Get additional services
                val tambahanArray = it.getSerializableExtra("layananTambahan") as? ArrayList<*>
                tambahanArray?.let { list ->
                    tambahanList.clear()
                    list.forEach { item ->
                        if (item is ModelTambahan) {
                            tambahanList.add(item)
                            hargaTotal += item.hargaTambahan?.toIntOrNull() ?: 0
                        }
                    }
                    adapter.updateData(tambahanList)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun calculateTotal() {
        tvTotalBayar.text = formatRupiah(hargaTotal.toDouble())
    }

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number)
    }
    private fun setupButtonListeners() {
        bBatal.setOnClickListener {
            finish()
        }

        bPembayaran.setOnClickListener {
            showPaymentDialog()
        }
    }

    private fun showPaymentDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_mod_pembayaran)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)

        // Inisialisasi tombol-tombol pembayaran
        val bBayarNanti = dialog.findViewById<Button>(R.id.bBayarNanti)
        val bTunai = dialog.findViewById<Button>(R.id.bTunai)
        val bQRIS = dialog.findViewById<Button>(R.id.bQRIS)
        val bDana = dialog.findViewById<Button>(R.id.bDana)
        val bGopay = dialog.findViewById<Button>(R.id.bGopay)
        val bOVO = dialog.findViewById<Button>(R.id.bOVO)// Pastikan ada TextView dengan id tvBatal di XML

        // Set listener untuk setiap metode pembayaran
        bBayarNanti.setOnClickListener {
            processPayment("Bayar Nanti")
        }

        bTunai.setOnClickListener {
            processPayment("Tunai")
        }

        bQRIS.setOnClickListener {
            processPayment("QRIS")
        }

        bDana.setOnClickListener {
            processPayment("DANA")
        }

        bGopay.setOnClickListener {
            processPayment("GoPay")
        }

        bOVO.setOnClickListener {
            processPayment("OVO")
        }
        val tvBatalDialog = dialog.findViewById<TextView>(R.id.tvBatal)
        tvBatalDialog.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun processPayment(method: String) {
        val tanggalTransaksi = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val statusPembayaran = if (method == "Bayar Nanti") "Belum Dibayar" else "Sudah Dibayar"

        // Hitung total tambahan
        val totalTambahan = tambahanList.sumOf { it.hargaTambahan?.toIntOrNull() ?: 0 }
        val hargaUtama = intent.getStringExtra("hargaLayanan")?.toIntOrNull() ?: 0
        val totalBayar = hargaUtama + totalTambahan

        // Buat ID unik untuk transaksi
        val idTransaksi = "TRX-${System.currentTimeMillis()}"

        // Buat objek data untuk Firebase
        val laporanData = hashMapOf<String, Any>(
            "idTransaksi" to idTransaksi,
            "namaPelanggan" to (tvNamaPelanggan.text.toString()),
            "noHpPelanggan" to (tvNoHp.text.toString()),
            "namaLayanan" to (intent.getStringExtra("namaLayanan") ?: "-"),
            "hargaLayanan" to (intent.getStringExtra("hargaLayanan") ?: "0"),
            "totalTambahan" to tambahanList.size.toString(),
            "totalBayar" to totalBayar.toString(),
            "metodePembayaran" to method,
            "statusPembayaran" to statusPembayaran,
            "tanggalTransaksi" to tanggalTransaksi
        )

        // Simpan ke Firebase
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("laporan").child(idTransaksi)
        reference.setValue(laporanData)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.toast_transaksiberhasil), Toast.LENGTH_SHORT).show()

                // Lanjutkan ke intent berikutnya
                val notaIntent = Intent(this, NotaTransaksi::class.java).apply {
                    putExtra("idTransaksi", idTransaksi)
                    putExtra("namaPelanggan", tvNamaPelanggan.text.toString())
                    putExtra("nohpPelanggan", tvNoHp.text.toString())
                    putExtra("namaLayanan", intent.getStringExtra("namaLayanan") ?: "-")
                    putExtra("hargaLayanan", intent.getStringExtra("hargaLayanan") ?: "0")
                    putExtra("totalTambahan", totalTambahan.toString())
                    putExtra("totalBayar", totalBayar.toString())
                    putExtra("metodePembayaran", method)
                    putExtra("statusPembayaran", statusPembayaran)
                    putExtra("tanggalTransaksi", tanggalTransaksi)
                    if (tambahanList.isNotEmpty()) {
                        putExtra("layananTambahan", ArrayList(tambahanList))
                    }
                }

                startActivity(notaIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, getString(R.string.toast_transaksigagal), Toast.LENGTH_SHORT).show()
            }
    }


    private fun saveToLaporan(method: String) {
        val sharedPref = getSharedPreferences("data_laporan", MODE_PRIVATE)
        val existingData = sharedPref.getString("laporan", "[]") ?: "[]"
        val jsonArray = JSONArray(existingData)

        val idTransaksi = "TRX-${System.currentTimeMillis().toString().takeLast(6)}"
        val tanggalTransaksi = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
            Date()
        )

        // Hitung total
        val hargaUtama = intent.getStringExtra("hargaLayanan")?.toIntOrNull() ?: 0
        val totalTambahan = tambahanList.sumOf { it.hargaTambahan?.toIntOrNull() ?: 0 }
        val totalBayar = hargaUtama + totalTambahan

        val newLaporan = JSONObject().apply {
            put("idTransaksi", idTransaksi)
            put("namaPelanggan", tvNamaPelanggan.text.toString())
            put("noHpPelanggan", tvNoHp.text.toString())
            put("namaLayanan", intent.getStringExtra("namaLayanan") ?: "-")
            put("hargaLayanan", intent.getStringExtra("hargaLayanan") ?: "0")
            put("totalBayar", totalBayar.toString())
            put("metodePembayaran", method)
            put("statusPembayaran", "Belum Dibayar")
            put("tanggalTransaksi", tanggalTransaksi)
        }

        jsonArray.put(newLaporan)

        sharedPref.edit().putString("laporan", jsonArray.toString()).apply()
    }

}
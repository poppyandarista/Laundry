package com.luthfiana.laundry.transaksi

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luthfiana.laundry.R
import com.luthfiana.laundry.adapter.PilihTambahanAdapter
import com.luthfiana.laundry.modeldata.ModelTambahan
import java.net.URLEncoder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotaTransaksi : AppCompatActivity() {

    private lateinit var tvIDTransaksi: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvPesanan: TextView
    private lateinit var tvKaryawan: TextView
    private lateinit var tvHargaLayanan: TextView
    private lateinit var rvTambahan: RecyclerView
    private lateinit var tvSubtotalTambahan: TextView
    private lateinit var tvTotalBayar: TextView
    private lateinit var btnKirimWhatsapp: Button
    private lateinit var btnCetak: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nota_transaksi)

        // Inisialisasi view
        initViews()

        // Set tanggal otomatis
        setCurrentDate()

        // Terima dan tampilkan data dari intent
        displayDataFromIntent()

        // Di dalam onCreate() setelah displayDataFromIntent()
        btnKirimWhatsapp.setOnClickListener {
            // Implementasi kirim ke WhatsApp
            shareReceiptViaWhatsApp()
        }

        btnCetak.setOnClickListener {
            // Implementasi cetak
            printReceipt()
        }

        // Contoh di MainActivity.kt
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "User") ?: "User"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        tvIDTransaksi = findViewById(R.id.IDTransaksi_hasil_nota)
        tvTanggal = findViewById(R.id.Tanggal_hasil_nota)
        tvPesanan = findViewById(R.id.Pesanan_hasil_nota)
        tvKaryawan = findViewById(R.id.Karyawan_hasil_nota)
        tvHargaLayanan = findViewById(R.id.hargalayanan_hasil_nota)
        rvTambahan = findViewById(R.id.rvNota_hasil_pilihtamabahan)
        tvSubtotalTambahan = findViewById(R.id.subtotaltambahan_hasil_nota)
        tvTotalBayar = findViewById(R.id.totalbayar_hasil_nota)
        btnKirimWhatsapp = findViewById(R.id.bKirimWhatsapp)
        btnCetak = findViewById(R.id.bCetak)

        // Atur RecyclerView untuk layanan tambahan
        rvTambahan.layoutManager = LinearLayoutManager(this)
    }

    private fun setCurrentDate() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        tvTanggal.text = currentDate
    }

    private fun displayDataFromIntent() {
        intent?.let {
            // Tampilkan data pelanggan
            tvPesanan.text = it.getStringExtra("namaPelanggan") ?: "-"

            // Ambil data user dari SharedPreferences
            val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userName = sharedPref.getString("user_name", "-") ?: "-"
            val userBranch = sharedPref.getString("user_branch", "-") ?: "-"

            // Set nama karyawan dan cabang
            tvKaryawan.text = userName
            findViewById<TextView>(R.id.cabanglaundry_nota).text = userBranch

            // Tampilkan data layanan utama
            val namaLayanan = it.getStringExtra("namaLayanan") ?: "-"
            val hargaLayanan = it.getStringExtra("hargaLayanan") ?: "0"

            val tvNamaLayanan = findViewById<TextView>(R.id.tvLayanan_nota)
            val tvHargaLayanan = findViewById<TextView>(R.id.hargalayanan_hasil_nota)

            tvNamaLayanan.text = namaLayanan
            tvHargaLayanan.text = formatRupiah(hargaLayanan.toDoubleOrNull() ?: 0.0)

            // Tampilkan layanan tambahan jika ada
            val tambahanList = it.getSerializableExtra("layananTambahan") as? ArrayList<ModelTambahan>
            if (tambahanList != null && tambahanList.isNotEmpty()) {
                val adapter = PilihTambahanAdapter(tambahanList)
                rvTambahan.adapter = adapter

                val subtotal = tambahanList.sumOf { it.hargaTambahan?.toIntOrNull() ?: 0 }
                tvSubtotalTambahan.text = formatRupiah(subtotal.toDouble())
            } else {
                tvSubtotalTambahan.text = formatRupiah(0.0)
            }

            // Tampilkan total bayar
            val totalBayar = it.getStringExtra("totalBayar") ?: hargaLayanan
            tvTotalBayar.text = formatRupiah(totalBayar.toDoubleOrNull() ?: 0.0)

            // Generate ID transaksi
            val idTransaksi = "TRX-${System.currentTimeMillis().toString().takeLast(6)}"
            tvIDTransaksi.text = idTransaksi
        }
    }

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number)
    }



    // Tambahkan method ini di kelas NotaTransaksi
    private fun shareReceiptViaWhatsApp() {
        val noHpPelanggan = intent.getStringExtra("nohpPelanggan") ?: run {
            Toast.makeText(this, "Nomor HP pelanggan tidak tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        // Format nomor dengan cara yang sama seperti di adapter
        val formattedNoHp = noHpPelanggan
            .replace("\\s".toRegex(), "")
            .replace("-", "")
            .replace("[^\\d]".toRegex(), "")
            .let { if (it.startsWith("0")) "62${it.substring(1)}" else it }

        val receiptText = buildReceiptText()
        val url = "https://wa.me/$formattedNoHp?text=${Uri.encode(receiptText)}"

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            // Jangan paksa package WhatsApp, biarkan sistem memilih
            // packageManager?.let { pm ->
            //    if (intent.resolveActivity(pm) != null) {
            startActivity(intent)
            //    } else {
            //        Toast.makeText(this, "Tidak ada aplikasi yang dapat menangani permintaan ini", Toast.LENGTH_SHORT).show()
            //    }
            // }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildReceiptText(): String {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "-") ?: "-"
        val userBranch = sharedPref.getString("user_branch", "-") ?: "-"

        val sb = StringBuilder()
        sb.appendLine("⚡ *LA-LAUNDRY RECEIPT* ⚡")
        sb.appendLine("*Cabang: $userBranch*")
        sb.appendLine("")
        sb.appendLine("📋 *ID Transaksi:* ${tvIDTransaksi.text}")
        sb.appendLine("📅 *Tanggal:* ${tvTanggal.text}")
        sb.appendLine("👤 *Pelanggan:* ${tvPesanan.text}")
        sb.appendLine("👷 *Karyawan:* $userName")
        sb.appendLine("")
        sb.appendLine("🧺 *Layanan Utama:*")
        sb.appendLine("  - ${intent.getStringExtra("namaLayanan")}: ${intent.getStringExtra("hargaLayanan")}")

        val tambahanList = intent.getSerializableExtra("layananTambahan") as? ArrayList<ModelTambahan>
        if (!tambahanList.isNullOrEmpty()) {
            sb.appendLine("")
            sb.appendLine("➕ *Layanan Tambahan:*")
            tambahanList.forEach {
                sb.appendLine("  - ${it.namaLayananTambahan}: ${it.hargaTambahan}")
            }
        }

        sb.appendLine("")
        sb.appendLine("💵 *Total Bayar:* ${tvTotalBayar.text}")
        sb.appendLine("💳 *Metode Pembayaran:* ${intent.getStringExtra("metodePembayaran")}")
        sb.appendLine("")
        sb.appendLine("_Terima kasih telah menggunakan layanan kami_")

        return sb.toString()
    }

    private fun printReceipt() {
        // Implementasi cetak ke printer
        Toast.makeText(this, "Fitur cetak akan diimplementasikan", Toast.LENGTH_SHORT).show()
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        // Bersihkan nomor dari karakter non-digit secara lebih menyeluruh
        val cleanedNumber = phoneNumber
            .replace("\\s".toRegex(), "")  // Hapus spasi
            .replace("-", "")              // Hapus tanda minus
            .replace("[^\\d]".toRegex(), "") // Hapus semua non-digit

        // Jika nomor sudah termasuk kode negara (62), return langsung
        if (cleanedNumber.startsWith("62")) {
            return cleanedNumber
        }

        // Jika nomor diawali 0, ganti dengan 62
        if (cleanedNumber.startsWith("0")) {
            return "62" + cleanedNumber.substring(1)
        }

        // Jika nomor tidak diawali 0 atau 62, asumsikan sudah format internasional
        return cleanedNumber
    }


}
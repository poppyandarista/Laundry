package com.luthfiana.laundry.laporan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luthfiana.laundry.R
import com.luthfiana.laundry.adapter.DataLaporanAdapter
import com.luthfiana.laundry.modeldata.ModelLaporan

class DataLaporan : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DataLaporanAdapter
    private val laporanList = mutableListOf<ModelLaporan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_laporan)

        recyclerView = findViewById(R.id.rvDATA_LAPORAN)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DataLaporanAdapter(this, laporanList)
        recyclerView.adapter = adapter

        // Ambil data dari intent jika ada
        intent?.let {
            val newLaporan = ModelLaporan(
                idTransaksi = "TRX-${System.currentTimeMillis().toString().takeLast(6)}",
                namaPelanggan = it.getStringExtra("namaPelanggan") ?: "",
                noHpPelanggan = it.getStringExtra("nohpPelanggan") ?: "",
                namaLayanan = it.getStringExtra("namaLayanan") ?: "",
                totalBayar = it.getStringExtra("totalBayar") ?: "",
                metodePembayaran = it.getStringExtra("metodePembayaran") ?: "",
                statusPembayaran = it.getStringExtra("statusPembayaran") ?: "",
                tanggalTransaksi = it.getStringExtra("tanggalTransaksi") ?: ""
            )

            laporanList.add(newLaporan)
            saveDataToSharedPref(newLaporan)
            adapter.notifyDataSetChanged()
        }

        // Load data dari SharedPreferences
        loadDataFromSharedPref()
    }
    // Di DataLaporan.kt
    private fun loadDataFromSharedPref() {
        val sharedPref = getSharedPreferences("data_laporan", MODE_PRIVATE)
        val data = sharedPref.getStringSet("laporan_data", setOf()) ?: setOf()

        laporanList.clear()
        data.forEach { jsonString ->
            val parts = jsonString.split("|")
            if (parts.size == 6) {
                laporanList.add(ModelLaporan(
                    idTransaksi = parts[0],
                    namaPelanggan = parts[1],
                    totalBayar = parts[2],
                    metodePembayaran = parts[3],
                    statusPembayaran = parts[4],
                    tanggalTransaksi = parts[5]
                ))
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun saveDataToSharedPref(newLaporan: ModelLaporan) {
        val sharedPref = getSharedPreferences("data_laporan", MODE_PRIVATE)
        val existingData = sharedPref.getStringSet("laporan_data", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        val newData = "${newLaporan.idTransaksi}|${newLaporan.namaPelanggan}|${newLaporan.totalBayar}|" +
                "${newLaporan.metodePembayaran}|${newLaporan.statusPembayaran}|${newLaporan.tanggalTransaksi}"

        existingData.add(newData)
        sharedPref.edit().putStringSet("laporan_data", existingData).apply()
    }
}
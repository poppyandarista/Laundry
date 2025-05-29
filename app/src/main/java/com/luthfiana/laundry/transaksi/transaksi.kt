package com.luthfiana.laundry.transaksi

import android.content.Intent
import android.content.SharedPreferences
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
import com.google.firebase.FirebaseApp
import com.luthfiana.laundry.R
import com.luthfiana.laundry.adapter.PilihTambahanAdapter
import com.luthfiana.laundry.layanan.PilihLayanan
import com.luthfiana.laundry.modeldata.ModelTambahan
import com.luthfiana.laundry.pelanggan.PilihPelanggan
import com.luthfiana.laundry.tambahan.PilihTambahan

@Suppress("DEPRECATION")
class transaksi : AppCompatActivity() {
    private lateinit var tvPelangganNama: TextView
    private lateinit var tvPelangganNoHP: TextView
    private lateinit var tvhargaLayanan: TextView
    private lateinit var tvnamaLayanan: TextView
    private lateinit var bPilihPelanggan: Button
    private lateinit var bPilihLayanan: Button
    private lateinit var bTambahan: Button
    private lateinit var bProses: Button
    private lateinit var rvTransaksi_layanantambahan: RecyclerView
    private val dataList = mutableListOf<ModelTambahan>()
    private lateinit var adapter: PilihTambahanAdapter

    private val pilihPelanggan = 1
    private val pilihLayanan = 2
    private val pilihLayananTambahan = 3

    private var idPelanggan = ""
    private var idCabang = ""
    private var namaPelanggan = ""
    private var nohpPelanggan = ""
    private var idLayanan = ""
    private var namaLayanan = ""
    private var hargaLayanan = ""
    private var idPegawai = ""
    private lateinit var sharedPref: SharedPreferences

    private fun init() {
        bPilihPelanggan = findViewById(R.id.bPilihPelanggan)
        bPilihLayanan = findViewById(R.id.bPilihLayanan)
        bTambahan = findViewById(R.id.bTambahan)
        bProses = findViewById(R.id.bProses)
        rvTransaksi_layanantambahan = findViewById(R.id.rvTransaksi_layanantambahan)
        tvPelangganNama = findViewById(R.id.namapelanggan_transaksi)
        tvPelangganNoHP = findViewById(R.id.nohp_transaksi)
        tvnamaLayanan = findViewById(R.id.namalayanan_transaksi)
        tvhargaLayanan = findViewById(R.id.harga_transaksi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaksi)

        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        idCabang = sharedPref.getString("idCabang", "") ?: ""
        idPegawai = sharedPref.getString("idPegawai", "") ?: ""

        init()
        FirebaseApp.initializeApp(this)

        // Inisialisasi adapter dengan callback untuk delete
        adapter = PilihTambahanAdapter(ArrayList(dataList)).apply {
            setOnItemClickListener { _, position ->
                dataList.removeAt(position)
                updateData(dataList)
            }
        }

        rvTransaksi_layanantambahan.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = false
            reverseLayout = false
        }
        rvTransaksi_layanantambahan.adapter = adapter
        rvTransaksi_layanantambahan.setHasFixedSize(true)

        bPilihPelanggan.setOnClickListener {
            val intent = Intent(this, PilihPelanggan::class.java)
            startActivityForResult(intent, pilihPelanggan)
        }

        bPilihLayanan.setOnClickListener {
            val intent = Intent(this, PilihLayanan::class.java)
            startActivityForResult(intent, pilihLayanan)
        }

        bTambahan.setOnClickListener {
            val intent = Intent(this, PilihTambahan::class.java)
            startActivityForResult(intent, pilihLayananTambahan)
        }

        // In your transaksi activity, modify the bProses click listener:
        bProses.setOnClickListener {
            if (namaPelanggan.isNotEmpty() && nohpPelanggan.isNotEmpty() && namaLayanan.isNotEmpty()) {
                try {
                    val intent = Intent(this, KonfirmasiTransaksi::class.java).apply {
                        putExtra("namaPelanggan", namaPelanggan)
                        putExtra("nohpPelanggan", nohpPelanggan)
                        putExtra("namaLayanan", namaLayanan)
                        putExtra("hargaLayanan", hargaLayanan)
                        putExtra("layananTambahan", ArrayList(dataList)) // Pastikan dataList adalah ArrayList<ModelTambahan>
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Gagal membuka konfirmasi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (namaPelanggan.isEmpty()) {
                    Toast.makeText(this, "Silahkan Pilih Pelanggan Terlebih Dahulu", Toast.LENGTH_SHORT).show()
                    startActivityForResult(Intent(this, PilihPelanggan::class.java), pilihPelanggan)
                }
                if (namaLayanan.isEmpty()) {
                    Toast.makeText(this, "Silahkan Pilih Layanan Terlebih Dahulu", Toast.LENGTH_SHORT).show()
                    startActivityForResult(Intent(this, PilihLayanan::class.java), pilihLayanan)
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pilihPelanggan) {
            if (resultCode == RESULT_OK && data != null) {
                idPelanggan = data.getStringExtra("idPelanggan").orEmpty()
                val nama = data.getStringExtra("namaPelanggan").orEmpty()
                val nomorHP = data.getStringExtra("nohpPelanggan").orEmpty()

                tvPelangganNama.text = "Nama Pelanggan: $nama"
                tvPelangganNoHP.text = "No HP: $nomorHP"

                namaPelanggan = nama
                nohpPelanggan = nomorHP
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Batal memilih pelanggan", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == pilihLayanan) {
            if (resultCode == RESULT_OK && data != null) {
                idLayanan = data.getStringExtra("idLayanan").orEmpty()
                val nama = data.getStringExtra("namaLayanan").orEmpty()
                val harga = data.getStringExtra("hargaLayanan").orEmpty()

                tvnamaLayanan.text = "Nama Layanan: $nama"
                tvhargaLayanan.text = "Harga: $harga"

                namaLayanan = nama
                hargaLayanan = harga
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Batal memilih layanan", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == pilihLayananTambahan) {
            if (resultCode == RESULT_OK && data != null) {
                val idTambahan = data.getStringExtra("idTambahan").orEmpty()
                val namaTambahan = data.getStringExtra("namaLayananTambahan").orEmpty()
                val hargaTambahan = data.getStringExtra("hargaTambahan").orEmpty()

                dataList.add(
                    ModelTambahan(
                        idTambahan = idTambahan,
                        namaLayananTambahan = namaTambahan,
                        hargaTambahan = hargaTambahan
                    )
                )

                adapter.updateData(dataList)
            }
        }
    }
}
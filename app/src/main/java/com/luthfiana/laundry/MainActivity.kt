package com.luthfiana.laundry

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luthfiana.laundry.akun.akun
import com.luthfiana.laundry.cabang.DataCabang
import com.luthfiana.laundry.laporan.DataLaporan
import com.luthfiana.laundry.layanan.DataLayanan
import com.luthfiana.laundry.login.Login
import com.luthfiana.laundry.modeldata.ModelLaporan
import com.luthfiana.laundry.pegawai.DataPegawai
import com.luthfiana.laundry.pelanggan.DataPelanggan
import com.luthfiana.laundry.tambahan.DataTambahan
import com.luthfiana.laundry.transaksi.transaksi
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var tanggal: TextView
    private lateinit var tvTotalHariIni: TextView

    // CardViews for menu items
    private lateinit var cardTransaksi: CardView
    private lateinit var cardPelanggan: CardView
    private lateinit var cardLaporan: CardView
    private lateinit var cardAkun: CardView
    private lateinit var cardLayanan: CardView
    private lateinit var cardTambahan: CardView
    private lateinit var cardPegawai: CardView
    private lateinit var cardCabang: CardView
    private lateinit var cardPrinter: CardView

    // Container untuk tampilan pegawai
    private lateinit var pegawaiContainer: LinearLayout
    private lateinit var cardAkunPegawai: CardView
    private lateinit var cardPrinterPegawai: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check login status first
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        if (sharedPref.getString("user_phone", null) == null) {
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize views
        initViews()

        // Set up role-based access control
        setupRoleBasedAccess(sharedPref)

        calculateTodayTotal()

        // Set date and greeting
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        tanggal.text = dateFormat.format(Date())
        updateGreeting(sharedPref.getString("user_name", "User") ?: "User")

        // Set click listeners
        setupClickListeners()
    }

    private fun initViews() {
        tanggal = findViewById(R.id.tanggal)
        tvTotalHariIni = findViewById(R.id.tvjumlahestimasi)

        // Initialize all CardViews
        cardTransaksi = findViewById(R.id.cardview1)
        cardAkun = findViewById(R.id.akun)
        cardLayanan = findViewById(R.id.layanan)
        cardTambahan = findViewById(R.id.tambahan)
        cardPegawai = findViewById(R.id.pegawai)
        cardCabang = findViewById(R.id.cabang)
        cardPrinter = findViewById(R.id.printer)

        // Initialize pegawai container views
        pegawaiContainer = findViewById(R.id.pegawai_container)
        cardAkunPegawai = findViewById(R.id.akun_pegawai)
        cardPrinterPegawai = findViewById(R.id.printer_pegawai)
    }

    private fun setupRoleBasedAccess(sharedPref: SharedPreferences) {
        val userRole = sharedPref.getString("user_role", "pegawai") ?: "pegawai"
        Log.d("PrefCheck", "Stored role: $userRole")

        // Sembunyikan semua container terlebih dahulu
        pegawaiContainer.visibility = View.GONE
        findViewById<LinearLayout>(R.id.kepala_cabang_container).visibility = View.GONE

        when (userRole) {
            "admin" -> {
                // Admin bisa akses semua
                setAllCardsVisible(true)
            }
            "kepala_cabang" -> {
                // Sembunyikan semua card utama
                setAllCardsVisible(false)

                // Tampilkan container khusus kepala cabang
                findViewById<LinearLayout>(R.id.kepala_cabang_container).visibility = View.VISIBLE

                // Tetap tampilkan card transaksi utama
                cardTransaksi.visibility = View.VISIBLE
                cardAkun.visibility = View.VISIBLE
                cardLayanan.visibility = View.VISIBLE
                cardTambahan.visibility = View.VISIBLE
            }
            "pegawai" -> {
                // Sembunyikan semua card utama
                setAllCardsVisible(false)

                // Tampilkan container khusus pegawai
                pegawaiContainer.visibility = View.VISIBLE

                // Tetap tampilkan card transaksi utama
                cardTransaksi.visibility = View.VISIBLE
            }
            else -> {
                // Role tidak dikenali
                Toast.makeText(this, "Role \"$userRole\" tidak dikenali, akses dibatasi.", Toast.LENGTH_LONG).show()
                setAllCardsVisible(false)
                pegawaiContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun setAllCardsVisible(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        cardTransaksi.visibility = visibility
        cardAkun.visibility = visibility
        cardLayanan.visibility = visibility
        cardTambahan.visibility = visibility
        cardPegawai.visibility = visibility
        cardCabang.visibility = visibility
        cardPrinter.visibility = visibility
    }

    private fun updateGreeting(userName: String) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val textSelamat: TextView = findViewById(R.id.selamat)
        val greeting = when (hour) {
            in 5..11 -> getString(R.string.greeting_morning)
            in 12..15 -> getString(R.string.greeting_afternoon)
            in 16..18 -> getString(R.string.greeting_evening)
            else -> getString(R.string.greeting_night)
        }
        textSelamat.text = "$greeting $userName!"
    }

    private fun setupClickListeners() {
        // Transaksi
        findViewById<ImageView>(R.id.ivTransaksi).setOnClickListener {
            startActivity(Intent(this, transaksi::class.java))
        }

        // Pelanggan
        findViewById<ImageView>(R.id.ivPelanggan).setOnClickListener {
            startActivity(Intent(this, DataPelanggan::class.java))
        }

        // Laporan
        findViewById<ImageView>(R.id.ivLaporan).setOnClickListener {
            startActivity(Intent(this, DataLaporan::class.java))
        }

        // Akun (utama)
        cardAkun.setOnClickListener {
            startActivity(Intent(this, akun::class.java))
        }

        // Akun (pegawai)
        cardAkunPegawai.setOnClickListener {
            startActivity(Intent(this, akun::class.java))
        }

        // Layanan
        findViewById<ImageView>(R.id.ivLayanan).setOnClickListener {
            startActivity(Intent(this, DataLayanan::class.java))
        }

        // Tambahan
        findViewById<ImageView>(R.id.ivTambahan).setOnClickListener {
            startActivity(Intent(this, DataTambahan::class.java))
        }

        // Pegawai
        findViewById<ImageView>(R.id.ivPegawai).setOnClickListener {
            startActivity(Intent(this, DataPegawai::class.java))
        }

        // Cabang
        findViewById<ImageView>(R.id.ivCabang).setOnClickListener {
            startActivity(Intent(this, DataCabang::class.java))
        }

        // Printer (utama)
        cardPrinter.setOnClickListener {
            // Handle printer functionality
            Toast.makeText(this, "Printer functionality", Toast.LENGTH_SHORT).show()
        }

        // Printer (pegawai)
        cardPrinterPegawai.setOnClickListener {
            // Handle printer functionality
            Toast.makeText(this, "Printer functionality", Toast.LENGTH_SHORT).show()
        }

        findViewById<CardView>(R.id.pegawai_kepala).setOnClickListener {
            startActivity(Intent(this, DataPegawai::class.java))
        }

        // Printer (kepala cabang)
        findViewById<CardView>(R.id.printer_kepala).setOnClickListener {
            Toast.makeText(this, "Printer functionality", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateTodayTotal() {
        val database = FirebaseDatabase.getInstance().reference.child("laporan")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var total = 0.0

                for (data in snapshot.children) {
                    val laporan = data.getValue(ModelLaporan::class.java)
                    laporan?.let {
                        if (it.tanggalTransaksi?.startsWith(today) == true &&
                            it.statusPembayaran == "Sudah Dibayar") {
                            val amount = it.totalBayar?.toDoubleOrNull() ?: 0.0
                            total += amount
                        }
                    }
                }

                val localeID = Locale("in", "ID")
                val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
                tvTotalHariIni.text = formatRupiah.format(total)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        updateGreeting(sharedPref.getString("user_name", "User") ?: "User")
    }
}
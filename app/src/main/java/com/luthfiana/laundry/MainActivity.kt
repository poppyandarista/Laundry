package com.luthfiana.laundry

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.luthfiana.laundry.cabang.DataCabang
import com.luthfiana.laundry.layanan.DataLayanan
import com.luthfiana.laundry.pegawai.DataPegawai
import com.luthfiana.laundry.pelanggan.DataPelanggan
import com.luthfiana.laundry.tambahan.DataTambahan
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var tanggal: TextView
    lateinit var ivPelanggan: ImageView
    lateinit var ivPegawai: ImageView
    lateinit var ivCabang: ImageView
    lateinit var ivLayanan: ImageView
    lateinit var ivTambahan: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        tanggal = findViewById(R.id.tanggal)
        ivPelanggan = findViewById(R.id.ivPelanggan)
        ivPegawai = findViewById(R.id.ivPegawai)
        ivTambahan = findViewById(R.id.ivTambahan)
        ivCabang = findViewById(R.id.ivCabang)
        ivLayanan = findViewById(R.id.ivLayanan)

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        tanggal.text = currentDate

        val textSelamat: TextView = findViewById(R.id.selamat)

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting = when (hour) {
            in 5..11 -> "Selamat Pagi,"
            in 12..15 -> "Selamat Siang,"
            in 16..18 -> "Selamat Sore,"
            else -> "Selamat Malam,"
        }

        textSelamat.text = "$greeting Poppy!"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }
        val ivPelanggan: ImageView = findViewById(R.id.ivPelanggan)
        ivPelanggan.setOnClickListener {
            val intent = Intent(this@MainActivity, DataPelanggan::class.java)
            startActivity(intent)
        }
        val ivPegawai: ImageView = findViewById(R.id.ivPegawai)
        ivPegawai.setOnClickListener {
            val intent = Intent(this@MainActivity, DataPegawai::class.java)
            startActivity(intent)
        }
        val ivTambahan: ImageView = findViewById(R.id.ivTambahan)
        ivTambahan.setOnClickListener {
            val intent = Intent(this@MainActivity, DataTambahan::class.java)
            startActivity(intent)
        }
        val ivLayanan: ImageView = findViewById(R.id.ivLayanan)
        ivLayanan.setOnClickListener {
            val intent = Intent(this@MainActivity, DataLayanan::class.java)
            startActivity(intent)
        }
        val ivCabang: ImageView = findViewById(R.id.ivCabang)
        ivCabang.setOnClickListener {
            val intent = Intent(this@MainActivity, DataCabang::class.java)
            startActivity(intent)
        }
    }
}
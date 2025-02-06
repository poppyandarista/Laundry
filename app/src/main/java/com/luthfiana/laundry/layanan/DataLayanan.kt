package com.luthfiana.laundry.layanan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.luthfiana.laundry.R
import com.luthfiana.laundry.pegawai.tambah_pegawai

class DataLayanan : AppCompatActivity() {
    lateinit var fabTambahLayanan: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_layanan)

        fabTambahLayanan = findViewById(R.id.fabTambahLayanan)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fabTambahLayanan.setOnClickListener {
            val intent = Intent(this@DataLayanan, TambahLayanan::class.java)
            startActivity(intent)
        }
    }
}
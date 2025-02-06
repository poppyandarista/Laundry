package com.luthfiana.laundry.tambahan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.luthfiana.laundry.R
import com.luthfiana.laundry.pelanggan.TambahPelanggan

class DataTambahan : AppCompatActivity() {
    lateinit var fabTambahTambahan: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_tambahan)

        fabTambahTambahan = findViewById(R.id.fabTambahTambahan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fabTambahTambahan.setOnClickListener {
            val intent = Intent(this@DataTambahan, TambahTambahan::class.java)
            startActivity(intent)
        }
    }
}
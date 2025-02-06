package com.luthfiana.laundry.cabang

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.luthfiana.laundry.R
import com.luthfiana.laundry.layanan.TambahLayanan

class DataCabang : AppCompatActivity() {
    lateinit var fabTambahCabang: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_cabang)

        fabTambahCabang = findViewById(R.id.fabTambahCabang)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fabTambahCabang.setOnClickListener {
            val intent = Intent(this@DataCabang, TambahCabang::class.java)
            startActivity(intent)
        }
    }
}
package com.luthfiana.laundry.layanan

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.luthfiana.laundry.R
import com.luthfiana.laundry.modeldata.ModelLayanan

class TambahLayanan : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("layanan")

    lateinit var tvTambahLayanan: TextView
    lateinit var etNamaLayanan: EditText
    lateinit var etCabangLayanan: EditText
    lateinit var etHargaLayanan: EditText
    lateinit var bSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tambah_layanan)

        init()
        getData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bSimpan.setOnClickListener {
            if (cekValidasi()) {
                simpan()
            }
        }
    }

    fun init() {
        tvTambahLayanan = findViewById(R.id.tvTambahLayanan)
        etNamaLayanan = findViewById(R.id.etNamaLayanan)
        etCabangLayanan = findViewById(R.id.etCabangLayanan)
        etHargaLayanan = findViewById(R.id.etHargaLayanan)
        bSimpan = findViewById(R.id.bSimpan)
    }

    fun getData() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val layananList = mutableListOf<ModelLayanan>()
                    for (dataSnapshot in snapshot.children) {
                        val layanan = dataSnapshot.getValue(ModelLayanan::class.java)
                        layanan?.let { layananList.add(it) }
                    }
                    Log.d("Data Layanan", "Data ditemukan: $layananList")
                } else {
                    Log.d("Data Layanan", "Tidak ada data layanan")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database Error", error.message)
                Toast.makeText(this@TambahLayanan, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun cekValidasi(): Boolean {
        val nama = etNamaLayanan.text.toString()
        val cabang = etCabangLayanan.text.toString()
        val harga = etHargaLayanan.text.toString()

        if (nama.isEmpty()) {
            etNamaLayanan.error = getString(R.string.card_layanan_namalayanan)
            showToast(R.string.card_layanan_namalayanan)
            etNamaLayanan.requestFocus()
            return false
        }
        if (cabang.isEmpty()) {
            etCabangLayanan.error = getString(R.string.nama_cabang)
            showToast(R.string.nama_cabang)
            etCabangLayanan.requestFocus()
            return false
        }
        if (harga.isEmpty()) {
            etHargaLayanan.error = getString(R.string.card_layanan_harga)
            showToast(R.string.card_layanan_harga)
            etHargaLayanan.requestFocus()
            return false
        }
        return true
    }

    fun simpan() {
        val layananBaru = myRef.push()
        val layananId = layananBaru.key ?: return

        val data = ModelLayanan(
            layananId,
            etNamaLayanan.text.toString(),
            etCabangLayanan.text.toString(),
            etHargaLayanan.text.toString(),
        )

        layananBaru.setValue(data)
            .addOnSuccessListener {
                showToast(R.string.sukses_simpan_layanan)
                finish()
            }
            .addOnFailureListener {
                showToast(R.string.gagal_simpan_layanan)
            }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, getString(messageResId), Toast.LENGTH_SHORT).show()
    }
}

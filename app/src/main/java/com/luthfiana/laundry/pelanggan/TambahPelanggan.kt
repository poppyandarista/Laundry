package com.luthfiana.laundry.pelanggan

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
import com.luthfiana.laundry.modeldata.ModelPelanggan


class TambahPelanggan : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pelanggan")

    lateinit var tvTambahPelanggan: TextView
    lateinit var etNama: EditText
    lateinit var etAlamat: EditText
    lateinit var etNohp: EditText
    lateinit var etNamaCabang: EditText
    lateinit var bSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_pelanggan)

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
        tvTambahPelanggan = findViewById(R.id.tvTambahPelanggan)
        etNama = findViewById(R.id.etNama)
        etAlamat = findViewById(R.id.etAlamat)
        etNohp = findViewById(R.id.etNohp)
        etNamaCabang = findViewById(R.id.etNamaCabang)
        bSimpan = findViewById(R.id.bSimpan)
    }

    fun getData() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val pelangganList = mutableListOf<ModelPelanggan>()
                    for (dataSnapshot in snapshot.children) {
                        val pelanggan = dataSnapshot.getValue(ModelPelanggan::class.java)
                        pelanggan?.let { pelangganList.add(it) }
                    }
                    Log.d("Data Pelanggan", "Data ditemukan: $pelangganList")
                } else {
                    Log.d("Data Pelanggan", "Tidak ada data pelanggan")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database Error", error.message)
                Toast.makeText(this@TambahPelanggan, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun cekValidasi(): Boolean {
        val nama = etNama.text.toString()
        val alamat = etAlamat.text.toString()
        val nohp = etNohp.text.toString()
        val namaCabang = etNamaCabang.text.toString()

        if (nama.isEmpty()) {
            etNama.error = getString(R.string.card_pelanggan_nama_pelanggan)
            showToast(R.string.card_pelanggan_nama_pelanggan)
            etNama.requestFocus()
            return false
        }
        if (alamat.isEmpty()) {
            etAlamat.error = getString(R.string.card_pelanggan_alamat)
            showToast(R.string.card_pelanggan_alamat)
            etAlamat.requestFocus()
            return false
        }
        if (nohp.isEmpty()) {
            etNohp.error = getString(R.string.card_pelanggan_nohp)
            showToast(R.string.card_pelanggan_nohp)
            etNohp.requestFocus()
            return false
        }
        if (namaCabang.isEmpty()) {
            etNamaCabang.error = getString(R.string.nama_cabang)
            showToast(R.string.nama_cabang)
            etNamaCabang.requestFocus()
            return false
        }
        return true
    }

    fun simpan() {
        val pelangganBaru = myRef.push()
        val pelangganId = pelangganBaru.key ?: return

        val currentTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        val data = ModelPelanggan(
            pelangganId,
            etNama.text.toString(),
            etAlamat.text.toString(),
            etNohp.text.toString(),
            etNamaCabang.text.toString(),
            currentTime
        )

        pelangganBaru.setValue(data)
            .addOnSuccessListener {
                showToast(R.string.sukses_simpan_pelanggan)
                finish()
            }
            .addOnFailureListener {
                showToast(R.string.gagal_simpan_pelanggan)
            }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, getString(messageResId), Toast.LENGTH_SHORT).show()
    }
}

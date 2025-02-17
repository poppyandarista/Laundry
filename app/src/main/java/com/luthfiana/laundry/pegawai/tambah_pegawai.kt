package com.luthfiana.laundry.pegawai

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
import com.luthfiana.laundry.modeldata.ModelPegawai

class tambah_pegawai : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pegawai")

    lateinit var tvTambahPegawai: TextView
    lateinit var etNama: EditText
    lateinit var etAlamat: EditText
    lateinit var etNohp: EditText
    lateinit var etNamaCabang: EditText
    lateinit var bSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_pegawai)

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
        tvTambahPegawai = findViewById(R.id.tvTambahPegawai)
        etNama = findViewById(R.id.etNamaPegawai)
        etAlamat = findViewById(R.id.etAlamatPegawai)
        etNohp = findViewById(R.id.etNohpPegawai)
        etNamaCabang = findViewById(R.id.etNamaCabangPegawai)
        bSimpan = findViewById(R.id.bSimpan)
    }

    fun getData() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val pegawaiList = mutableListOf<ModelPegawai>()
                    for (dataSnapshot in snapshot.children) {
                        val pegawai = dataSnapshot.getValue(ModelPegawai::class.java)
                        pegawai?.let { pegawaiList.add(it) }
                    }
                    Log.d("Data Pegawai", "Data ditemukan: $pegawaiList")
                } else {
                    Log.d("Data Pegawai", "Tidak ada data pegawai")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Database Error", error.message)
                Toast.makeText(this@tambah_pegawai, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun cekValidasi(): Boolean {
        val nama = etNama.text.toString()
        val alamat = etAlamat.text.toString()
        val nohp = etNohp.text.toString()
        val namaCabang = etNamaCabang.text.toString()

        if (nama.isEmpty()) {
            etNama.error = getString(R.string.card_pegawai_nama)
            showToast(R.string.card_pegawai_nama)
            etNama.requestFocus()
            return false
        }
        if (alamat.isEmpty()) {
            etAlamat.error = getString(R.string.card_pegawai_alamat)
            showToast(R.string.card_pegawai_alamat)
            etAlamat.requestFocus()
            return false
        }
        if (nohp.isEmpty()) {
            etNohp.error = getString(R.string.card_pegawai_nohp)
            showToast(R.string.card_pegawai_nohp)
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
        val pegawaiBaru = myRef.push()
        val pegawaiId = pegawaiBaru.key ?: return

        val currentTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        val data = ModelPegawai(
            pegawaiId,
            etNama.text.toString(),
            etAlamat.text.toString(),
            etNohp.text.toString(),
            etNamaCabang.text.toString(),
            currentTime
        )

        pegawaiBaru.setValue(data)
            .addOnSuccessListener {
                showToast(R.string.sukses_simpan_pegawai)
                finish()
            }
            .addOnFailureListener {
                showToast(R.string.gagal_simpan_pegawai)
            }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, getString(messageResId), Toast.LENGTH_SHORT).show()
    }
}

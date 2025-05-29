package com.luthfiana.laundry.cabang

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import com.luthfiana.laundry.R
import com.luthfiana.laundry.modeldata.ModelCabang

class TambahCabang : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("cabang")

    private lateinit var tvTambahCabang: TextView
    private lateinit var etNamaCabang: EditText
    private lateinit var etAlamatCabang: EditText
    private lateinit var etNohpCabang: EditText
    private lateinit var bSimpan: Button

    private var idCabang: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_cabang)

        init()
        getData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bSimpan.setOnClickListener {
            cekValidasi()
        }
    }

    private fun init() {
        tvTambahCabang = findViewById(R.id.tvTambahCabang)
        etNamaCabang = findViewById(R.id.etNamaCabang)
        etAlamatCabang = findViewById(R.id.etAlamatCabang)
        etNohpCabang = findViewById(R.id.etNohpCabang)
        bSimpan = findViewById(R.id.bSimpan)
    }

    private fun getData() {
        idCabang = intent.getStringExtra("idCabang").orEmpty()

        val judul = intent.getStringExtra("judul")
        val nama = intent.getStringExtra("namaCabang")
        val alamat = intent.getStringExtra("alamatCabang")
        val nohp = intent.getStringExtra("nohpCabang")

        tvTambahCabang.text = judul
        etNamaCabang.setText(nama)
        etAlamatCabang.setText(alamat)
        etNohpCabang.setText(nohp)

        if (judul == "Edit Cabang") {
            mati()
            bSimpan.text = "Sunting"
        } else {
            hidup()
            etNamaCabang.requestFocus()
            bSimpan.text = "Simpan"
        }
    }

    private fun cekValidasi(): Boolean {
        val namaCabang = etNamaCabang.text.toString()
        val alamatCabang = etAlamatCabang.text.toString()
        val nohpCabang = etNohpCabang.text.toString()

        if (namaCabang.isEmpty()) {
            etNamaCabang.error = getString(R.string.nama_cabang)
            showToast(R.string.nama_cabang)
            etNamaCabang.requestFocus()
            return false
        }

        if (alamatCabang.isEmpty()) {
            etAlamatCabang.error = getString(R.string.tvAlamatCabang)
            showToast(R.string.tvAlamatCabang)
            etAlamatCabang.requestFocus()
            return false
        }

        if (nohpCabang.isEmpty()) {
            etNohpCabang.error = getString(R.string.tvNoHpCabang)
            showToast(R.string.tvNoHpCabang)
            etNohpCabang.requestFocus()
            return false
        }

        when (bSimpan.text.toString()) {
            "Simpan" -> simpan()
            "Sunting" -> {
                hidup()
                bSimpan.text = "Perbarui"
            }
            "Perbarui" -> update()
        }

        return true
    }

    private fun mati() {
        etNamaCabang.isEnabled = false
        etAlamatCabang.isEnabled = false
        etNohpCabang.isEnabled = false
    }

    private fun hidup() {
        etNamaCabang.isEnabled = true
        etAlamatCabang.isEnabled = true
        etNohpCabang.isEnabled = true
    }

    private fun update() {
        val cabangRef = database.getReference("cabang").child(idCabang)

        val data = ModelCabang(
            idCabang,
            etNamaCabang.text.toString(),
            etAlamatCabang.text.toString(),
            etNohpCabang.text.toString()
        )

        val updateData = mapOf(
            "namaCabang" to data.namaCabang,
            "alamatCabang" to data.alamatCabang,
            "nohpCabang" to data.nohpCabang
        )

        cabangRef.updateChildren(updateData)
            .addOnSuccessListener {
                Toast.makeText(this, "Data Cabang berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memperbarui data cabang", Toast.LENGTH_SHORT).show()
            }
    }

    private fun simpan() {
        val cabangBaru = myRef.push()
        val cabangId = cabangBaru.key ?: return

        val data = ModelCabang(
            cabangId,
            etNamaCabang.text.toString(),
            etAlamatCabang.text.toString(),
            etNohpCabang.text.toString()
        )

        cabangBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    getString(R.string.sukses_simpan_cabang),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    getString(R.string.gagal_simpan_cabang),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, getString(messageResId), Toast.LENGTH_SHORT).show()
    }
}

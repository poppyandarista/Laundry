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

    var idPegawai: String=""

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
            cekValidasi()

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
        idPegawai = intent.getStringExtra("idPegawai").toString()
        val judul = intent.getStringExtra("judul")
        val nama = intent.getStringExtra("namaPegawai")
        val alamat = intent.getStringExtra("alamatPegawai")
        val nohp = intent.getStringExtra("nohpPegawai")
        val namaCabang = intent.getStringExtra("idCabang")

        tvTambahPegawai.text = judul
        etNama.setText(nama)
        etAlamat.setText(alamat)
        etNohp.setText(nohp)
        etNamaCabang.setText(namaCabang)

        if (!tvTambahPegawai.text.equals(this.getString(R.string.tv_tambah_pegawai))) {
            if (judul.equals(this.getString(R.string.editpegawai))) {
                    mati()
                    bSimpan.text = this.getString(R.string.sunting)
                }
            } else {
            hidup()
            etNama.requestFocus()
            bSimpan.text = this.getString(R.string.simpan)
        }

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
            Toast.makeText(this, getString(R.string.card_pegawai_nama), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return false
        }
        if (alamat.isEmpty()) {
            etAlamat.error = getString(R.string.card_pegawai_alamat)
            Toast.makeText(this, getString(R.string.card_pegawai_alamat), Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return false
        }
        if (nohp.isEmpty()) {
            etNohp.error = getString(R.string.card_pegawai_nohp)
            Toast.makeText(this, getString(R.string.card_pegawai_nohp), Toast.LENGTH_SHORT).show()
            etNohp.requestFocus()
            return false
        }
        if (namaCabang.isEmpty()) {
            etNamaCabang.error = getString(R.string.nama_cabang)
            Toast.makeText(this, getString(R.string.nama_cabang), Toast.LENGTH_SHORT).show()
            etNamaCabang.requestFocus()
            return false
        }

        if (bSimpan.text == this.getString(R.string.simpan)) {
            simpan()
        } else if (bSimpan.text == this.getString(R.string.sunting)) {
            hidup()
            bSimpan.text = this.getString(R.string.perbarui)
        } else if (bSimpan.text == this.getString(R.string.perbarui)) {
            update()
        }
        return true
    }

    fun mati(){
        etNama.isEnabled=false
        etAlamat.isEnabled=false
        etNohp.isEnabled=false
        etNamaCabang.isEnabled=false
    }

    fun hidup(){
        etNama.isEnabled=true
        etAlamat.isEnabled=true
        etNohp.isEnabled=true
        etNamaCabang.isEnabled=true
    }

    fun update() {
        val pegawaiRef = database.getReference("pegawai").child(idPegawai)
        val currentTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        val data = ModelPegawai(
            idPegawai,
            etNama.text.toString(),
            etAlamat.text.toString(),
            etNohp.text.toString(),
            etNamaCabang.text.toString(),
            currentTime
        )

        val updateData = mapOf(
            "namaPegawai" to data.namaPegawai,
            "alamatPegawai" to data.alamatPegawai,
            "nohpPegawai" to data.nohpPegawai,
            "idCabang" to data.idCabang
        )

        pegawaiRef.updateChildren(updateData)
            .addOnSuccessListener {
                Toast.makeText(this, "Data Pegawai Berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memperbarui data pegawai", Toast.LENGTH_SHORT).show()
            }
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
                Toast.makeText(this, this.getString(R.string.sukses_simpan_pegawai), Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, this.getString(R.string.gagal_simpan_pegawai), Toast.LENGTH_SHORT).show()
            }

    }


    private fun showToast(messageResId: Int) {
        Toast.makeText(this, getString(messageResId), Toast.LENGTH_SHORT).show()
    }
}
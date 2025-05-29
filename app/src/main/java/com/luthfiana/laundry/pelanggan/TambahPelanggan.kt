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
    lateinit var etCabangPelanggan: EditText
    lateinit var etAlamat: EditText
    lateinit var etNohp: EditText
    lateinit var bSimpan: Button

    var idPelanggan: String = ""

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
            cekValidasi()

    }
    }

    fun init() {
        tvTambahPelanggan = findViewById(R.id.tvTambahPelanggan)
        etNama = findViewById(R.id.etNama)
        etCabangPelanggan = findViewById(R.id.etCabangPelanggan)
        etAlamat = findViewById(R.id.etAlamat)
        etNohp = findViewById(R.id.etNohp)
        bSimpan = findViewById(R.id.bSimpan)
    }

    fun getData() {
        idPelanggan = intent.getStringExtra("idPelanggan").toString()
        val judul = intent.getStringExtra("judul")
        val nama = intent.getStringExtra("namaPelanggan")
        val cabang = intent.getStringExtra("idCabang")
        val alamat = intent.getStringExtra("alamatPelanggan")
        val nohp = intent.getStringExtra("nohpPelanggan")

        tvTambahPelanggan.text = judul
        etNama.setText(nama)
        etCabangPelanggan.setText(cabang)
        etAlamat.setText(alamat)
        etNohp.setText(nohp)

        if (!tvTambahPelanggan.text.equals(this.getString(R.string.tvtambah_pelanggan))) {
            if (judul.equals("Edit Pelanggan")) {
                mati()
                bSimpan.text="Sunting"
            }
        }else{
            hidup()
                etNama.requestFocus()
                bSimpan.text="Simpan"
            }

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
        val cabang = etCabangPelanggan.text.toString()
        val alamat = etAlamat.text.toString()
        val nohp = etNohp.text.toString()

        if (nama.isEmpty()) {
            etNama.error = getString(R.string.card_pelanggan_nama_pelanggan)
            Toast.makeText(this, getString(R.string.card_pelanggan_nama_pelanggan), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return false
        }
        if (cabang.isEmpty()) {
            etCabangPelanggan.error = getString(R.string.card_cabang)
            Toast.makeText(this, getString(R.string.card_cabang), Toast.LENGTH_SHORT).show()
            etCabangPelanggan.requestFocus()
            return false
        }
        if (alamat.isEmpty()) {
            etAlamat.error = getString(R.string.card_pelanggan_alamat)
            Toast.makeText(this, getString(R.string.card_pelanggan_alamat), Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return false
        }
        if (nohp.isEmpty()) {
            etNohp.error = getString(R.string.card_pelanggan_nohp)
            Toast.makeText(this, getString(R.string.card_pelanggan_nohp), Toast.LENGTH_SHORT).show()
            etNohp.requestFocus()
            return false
        }
        if (bSimpan.text == "Simpan") {
            simpan()
        } else if (bSimpan.text == "Sunting") {
            hidup()
            bSimpan.text = "Perbarui"
        } else if (bSimpan.text == "Perbarui") {
            update()
        }
        return true
    }

        fun mati(){
            etNama.isEnabled=false
            etCabangPelanggan.isEnabled=false
            etAlamat.isEnabled=false
            etNohp.isEnabled=false
        }

        fun hidup(){
            etNama.isEnabled=true
            etCabangPelanggan.isEnabled=true
            etAlamat.isEnabled=true
            etNohp.isEnabled=true
        }

        fun update() {
            val pelangganRef = database.getReference("pelanggan").child(idPelanggan)
            val currentTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())

            val data = ModelPelanggan(
                idPelanggan,
                etNama.text.toString(),
                etCabangPelanggan.text.toString(),
                etAlamat.text.toString(),
                etNohp.text.toString(),
                currentTime
            )

            val updateData = mapOf(
                "namaPelanggan" to data.namaPelanggan,
                "idCabang" to data.idCabang,
                "alamatPelanggan" to data.alamatPelanggan,
                "nohpPelanggan" to data.nohpPelanggan,
            )

            pelangganRef.updateChildren(updateData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Pelanggan Berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal memperbarui data pelanggan", Toast.LENGTH_SHORT).show()
                }
        }

        fun simpan() {
            if (bSimpan.text.equals(this.getString(R.string.bsimpan))){
            val pelangganBaru = myRef.push()
            val pelangganId = pelangganBaru.key ?: return

            val currentTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())

            val data = ModelPelanggan(
                pelangganId,
                etNama.text.toString(),
                etCabangPelanggan.text.toString(),
                etAlamat.text.toString(),
                etNohp.text.toString(),
                currentTime
            )

            pelangganBaru.setValue(data)
                .addOnSuccessListener {
                    Toast.makeText(this, this.getString(R.string.sukses_simpan_pelanggan), Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, this.getString(R.string.gagal_simpan_pelanggan), Toast.LENGTH_SHORT).show()
                }
        }
        }


        private fun showToast(messageResId: Int) {
        Toast.makeText(this, getString(messageResId), Toast.LENGTH_SHORT).show()
    }
}

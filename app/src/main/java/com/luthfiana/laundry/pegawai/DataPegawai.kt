package com.luthfiana.laundry.pegawai

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luthfiana.laundry.R
import com.luthfiana.laundry.adapter.DataPegawaiAdapter
import com.luthfiana.laundry.modeldata.ModelPegawai

class DataPegawai : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pegawai")
    private lateinit var rvDataPegawai: RecyclerView
    private lateinit var pegawaiList: ArrayList<ModelPegawai>
    private lateinit var adapter: DataPegawaiAdapter
    lateinit var fabTambahPegawai: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pegawai)

        initViews()
        setupRecyclerView()
        getData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fabTambahPegawai.setOnClickListener {
            val intent = Intent(this@DataPegawai, tambah_pegawai::class.java)
            intent.putExtra("judul", this.getString(R.string.tv_tambah_pegawai))
            intent.putExtra("idPegawai", "")
            intent.putExtra("namaPegawai", "")
            intent.putExtra("alamatPegawai", "")
            intent.putExtra("nohpPegawai", "")
            intent.putExtra("idCabang", "")
            startActivity(intent)
        }
    }

    private fun initViews() {
        rvDataPegawai = findViewById(R.id.rvDATA_PEGAWAI)
        fabTambahPegawai = findViewById(R.id.fabTambahPegawai)
    }

    private fun setupRecyclerView() {
        rvDataPegawai.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        rvDataPegawai.setHasFixedSize(true)
        pegawaiList = ArrayList()
    }

    private fun getData() {
        val query = myRef.orderByChild("idPegawai").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    pegawaiList.clear()
                    for (childSnapshot in dataSnapshot.children) {
                        val pegawai = childSnapshot.getValue(ModelPegawai::class.java)
                        pegawai?.let { pegawaiList.add(it) }
                    }
                    rvDataPegawai.adapter = DataPegawaiAdapter(pegawaiList).also{
                        it.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@DataPegawai, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}

package com.luthfiana.laundry.pelanggan

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.luthfiana.laundry.R
import com.luthfiana.laundry.adapter.PilihPelangganAdapter
import com.luthfiana.laundry.modeldata.ModelPelanggan

class PilihPelanggan : AppCompatActivity() {

    private lateinit var tvKosong: TextView
    private lateinit var rvDATA_PILIH_PELANGGAN: RecyclerView
    private lateinit var idCabang: String
    private lateinit var searchView: SearchView
    private val pelangganList = ArrayList<ModelPelanggan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pilih_pelanggan)

        rvDATA_PILIH_PELANGGAN = findViewById(R.id.rvDATA_PILIH_PELANGGAN)
        tvKosong = findViewById(R.id.tvPILIH_PELANGGAN_kosong)
        searchView = findViewById<SearchView>(R.id.search_bar)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        idCabang = getSharedPreferences("user_data", MODE_PRIVATE)
            .getString("idCabang", "") ?: ""

        rvDATA_PILIH_PELANGGAN.layoutManager = LinearLayoutManager(this)
        getData()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterData(newText.orEmpty())
                return true
            }
        })

    }
    private fun filterData(query: String) {
        val filteredList = pelangganList.filter {
            it.namaPelanggan?.contains(query, ignoreCase = true) == true ||
                    it.nohpPelanggan?.contains(query, ignoreCase = true) == true
        }.reversed() // biar urutan tetap terbaru di atas

        rvDATA_PILIH_PELANGGAN.adapter = PilihPelangganAdapter(ArrayList(filteredList))
    }

    private fun getData() {
        val db = FirebaseDatabase.getInstance()
        val myRef = db.getReference("pelanggan")

        myRef.limitToLast(100).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pelangganList.clear()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val pelanggan = data.getValue(ModelPelanggan::class.java)
                        if (pelanggan != null) {
                            pelangganList.add(pelanggan)
                        }
                    }

                    // 🔄 BALIK urutan biar data baru muncul di atas

                    val reversedList = pelangganList.reversed() as ArrayList<ModelPelanggan>
                    rvDATA_PILIH_PELANGGAN.adapter = PilihPelangganAdapter(reversedList)

                    tvKosong.visibility = View.GONE
                } else {
                    tvKosong.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}

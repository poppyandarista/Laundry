package com.luthfiana.laundry.layanan

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
import com.luthfiana.laundry.adapter.PilihLayananAdapter
import com.luthfiana.laundry.modeldata.ModelLayanan

class PilihLayanan : AppCompatActivity() {

    private lateinit var tvKosong: TextView
    private lateinit var rvDATA_PILIH_LAYANAN: RecyclerView
    private lateinit var idCabang: String
    private lateinit var searchView: SearchView
    private val layananList = ArrayList<ModelLayanan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pilih_layanan)

        rvDATA_PILIH_LAYANAN = findViewById(R.id.rvDATA_PILIH_LAYANAN)
        tvKosong = findViewById(R.id.tvPILIH_LAYANAN_kosong)
        searchView = findViewById<SearchView>(R.id.search_barlayanan)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        idCabang = getSharedPreferences("user_data", MODE_PRIVATE)
            .getString("idCabang", "") ?: ""

        rvDATA_PILIH_LAYANAN.layoutManager = LinearLayoutManager(this)
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
        val filteredList = layananList.filter {
            it.namaLayanan?.contains(query, ignoreCase = true) == true ||
                    it.hargaLayanan?.contains(query, ignoreCase = true) == true
        }.reversed() // biar urutan tetap terbaru di atas

        rvDATA_PILIH_LAYANAN.adapter = PilihLayananAdapter(ArrayList(filteredList))
    }

    private fun getData() {
        val db = FirebaseDatabase.getInstance()
        val myRef = db.getReference("layanan")

        myRef.limitToLast(100).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                layananList.clear()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val layanan = data.getValue(ModelLayanan::class.java)
                        if (layanan != null) {
                            layananList.add(layanan)
                        }
                    }

                    // 🔄 BALIK urutan biar data baru muncul di atas

                    val reversedList = layananList.reversed() as ArrayList<ModelLayanan>
                    rvDATA_PILIH_LAYANAN.adapter = PilihLayananAdapter(reversedList)

                    tvKosong.visibility = View.GONE
                } else {
                    tvKosong.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}

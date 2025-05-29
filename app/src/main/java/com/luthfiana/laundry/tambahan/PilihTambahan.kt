package com.luthfiana.laundry.tambahan

import android.os.Bundle
import android.util.Log
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
import com.luthfiana.laundry.adapter.PilihTambahanAdapter
import com.luthfiana.laundry.modeldata.ModelTambahan

class PilihTambahan : AppCompatActivity() {

    private lateinit var tvKosong: TextView
    private lateinit var rvDATA_PILIH_TAMBAHAN: RecyclerView
    private lateinit var idCabang: String
    private lateinit var searchView: SearchView
    private val tambahanList = ArrayList<ModelTambahan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pilih_tambahan)

        rvDATA_PILIH_TAMBAHAN = findViewById(R.id.rvDATA_PILIH_TAMBAHAN)
        tvKosong = findViewById(R.id.tvPILIH_TAMBAHAN_kosong)
        searchView = findViewById<SearchView>(R.id.search_bartambahan)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        idCabang = getSharedPreferences("user_data", MODE_PRIVATE)
            .getString("idCabang", "") ?: ""

        rvDATA_PILIH_TAMBAHAN.layoutManager = LinearLayoutManager(this)
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
        val filteredList = tambahanList.filter {
            it.namaLayananTambahan?.contains(query, ignoreCase = true) == true ||
                    it.hargaTambahan?.contains(query, ignoreCase = true) == true
        }.reversed() // biar urutan tetap terbaru di atas

        rvDATA_PILIH_TAMBAHAN.adapter = PilihTambahanAdapter(ArrayList(filteredList))
    }

    private fun getData() {
        val db = FirebaseDatabase.getInstance()
        val myRef = db.getReference("tambahan")

        val query = myRef.orderByKey().limitToLast(100) // urut berdasarkan key (biasanya timestamp)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tambahanList.clear()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val tambahan = data.getValue(ModelTambahan::class.java)
                        if (tambahan != null) {
                            tambahanList.add(tambahan)
                        }
                    }
                    showEmptyState(false)
                    updateRecyclerView()
                } else {
                    showEmptyState(true)
                }
            }


            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDebug", "Database error: ${error.message}")
                showEmptyState(true)
            }
        })
    }

    private fun showEmptyState(show: Boolean) {
        runOnUiThread {
            tvKosong.visibility = if (show) View.VISIBLE else View.GONE
            rvDATA_PILIH_TAMBAHAN.visibility = if (show) View.GONE else View.VISIBLE
        }
    }


    private fun updateRecyclerView() {
        // ✅ Terbaru di atas (nomor besar di atas, kecil di bawah)
        val reversedList = tambahanList.reversed()
        rvDATA_PILIH_TAMBAHAN.adapter = PilihTambahanAdapter(ArrayList(reversedList))
    }
}

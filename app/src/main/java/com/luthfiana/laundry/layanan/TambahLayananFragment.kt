package com.luthfiana.laundry.layanan

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luthfiana.laundry.R
import com.luthfiana.laundry.modeldata.ModelLayanan

class TambahLayananFragment : Fragment() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("layanan")

    private lateinit var tvTambahLayanan: TextView
    private lateinit var etNamaLayanan: EditText
    private lateinit var etCabangLayanan: EditText
    private lateinit var etHargaLayanan: EditText
    private lateinit var bSimpan: Button

    private var idLayanan: String = ""
    private var isEditMode: Boolean = false

    companion object {
        fun newInstance(
            id: String,
            nama: String,
            cabang: String,
            harga: String
        ): TambahLayananFragment {
            val fragment = TambahLayananFragment()
            val args = Bundle()
            args.putString("idLayanan", id)
            args.putString("namaLayanan", nama)
            args.putString("namaCabang", cabang)
            args.putString("hargaLayanan", harga)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_tambah_layanan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        getData()

        bSimpan.setOnClickListener {
            cekValidasi()
        }
    }

    private fun initViews(view: View) {
        tvTambahLayanan = view.findViewById(R.id.tvTambahLayanan)
        etNamaLayanan = view.findViewById(R.id.etNamaLayanan)
        etCabangLayanan = view.findViewById(R.id.etCabangLayanan)
        etHargaLayanan = view.findViewById(R.id.etHargaLayanan)
        bSimpan = view.findViewById(R.id.bSimpan)
    }

    private fun getData() {
        arguments?.let {
            idLayanan = it.getString("idLayanan", "")
            val nama = it.getString("namaLayanan", "")
            val cabang = it.getString("namaCabang", "")
            val harga = it.getString("hargaLayanan", "")

            etNamaLayanan.setText(nama)
            etCabangLayanan.setText(cabang)
            etHargaLayanan.setText(harga)

            isEditMode = idLayanan.isNotEmpty()

            if (isEditMode) {
                tvTambahLayanan.text = getString(R.string.editlayanan)
                mati()
                bSimpan.text = getString(R.string.sunting)
            } else {
                tvTambahLayanan.text = getString(R.string.tvTambahLayanan)
                hidup()
                etNamaLayanan.requestFocus()
                bSimpan.text = getString(R.string.simpan)
            }
        }
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

        when (bSimpan.text.toString()) {
            getString(R.string.simpan) -> simpan()
            getString(R.string.sunting) -> {
                hidup()
                bSimpan.text = getString(R.string.perbarui)
            }

            getString(R.string.perbarui) -> update()
        }
        return true
    }

    fun mati() {
        etNamaLayanan.isEnabled = false
        etCabangLayanan.isEnabled = false
        etHargaLayanan.isEnabled = false
    }

    fun hidup() {
        etNamaLayanan.isEnabled = true
        etCabangLayanan.isEnabled = true
        etHargaLayanan.isEnabled = true
    }

    private fun update() {
        val layananRef = database.getReference("layanan").child(idLayanan)

        val data = ModelLayanan(
            idLayanan,
            etNamaLayanan.text.toString(),
            etCabangLayanan.text.toString(),
            etHargaLayanan.text.toString()
        )

        val updateData = mapOf(
            "namaLayanan" to data.namaLayanan,
            "namaCabang" to data.namaCabang,
            "hargaLayanan" to data.hargaLayanan
        )

        layananRef.updateChildren(updateData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                // Tutup fragment hanya jika dalam landscape
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    (activity as? DataLayanan)?.supportFragmentManager?.beginTransaction()
                        ?.remove(this)
                        ?.commit()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal memperbarui data Layanan", Toast.LENGTH_SHORT).show()
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FragmentLifecycle", "Fragment destroyed - ${this.hashCode()}")
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show()
    }

    private fun simpan() {
        val layananId = myRef.push().key ?: return

        val data = ModelLayanan(
            layananId,
            etNamaLayanan.text.toString(),
            etCabangLayanan.text.toString(),
            etHargaLayanan.text.toString()
        )

        myRef.child(layananId).setValue(data)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.sukses_simpan_layanan),
                    Toast.LENGTH_SHORT
                ).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.gagal_simpan_layanan),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}

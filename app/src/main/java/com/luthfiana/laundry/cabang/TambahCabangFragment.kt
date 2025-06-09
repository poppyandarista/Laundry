package com.luthfiana.laundry.cabang

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.luthfiana.laundry.R
import com.luthfiana.laundry.modeldata.ModelCabang

class TambahCabangFragment : Fragment() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("cabang")

    private lateinit var tvTambahCabang: TextView
    private lateinit var etNamaCabang: EditText
    private lateinit var etAlamatCabang: EditText
    private lateinit var etNohpCabang: EditText
    private lateinit var bSimpan: Button

    private var idCabang: String = ""

    companion object {
        fun newInstance(
            judul: String,
            idCabang: String,
            namaCabang: String,
            alamatCabang: String,
            nohpCabang: String
        ): TambahCabangFragment {
            val fragment = TambahCabangFragment()
            val args = Bundle()
            args.putString("judul", judul)
            args.putString("idCabang", idCabang)
            args.putString("namaCabang", namaCabang)
            args.putString("alamatCabang", alamatCabang)
            args.putString("nohpCabang", nohpCabang)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_tambah_cabang, container, false)

        tvTambahCabang = view.findViewById(R.id.tvTambahCabang)
        etNamaCabang = view.findViewById(R.id.etNamaCabang)
        etAlamatCabang = view.findViewById(R.id.etAlamatCabang)
        etNohpCabang = view.findViewById(R.id.etNohpCabang)
        bSimpan = view.findViewById(R.id.bSimpan)

        getData()

        bSimpan.setOnClickListener {
            cekValidasi()
        }

        return view
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

        if (bSimpan.text == getString(R.string.simpan)) {
            simpan()
        } else if (bSimpan.text == getString(R.string.sunting)) {
            hidup()
            bSimpan.text = getString(R.string.perbarui)
        } else if (bSimpan.text == getString(R.string.perbarui)) {
            update()
        }
        return true
    }

    private fun getData() {
        idCabang = arguments?.getString("idCabang").orEmpty()

        val judul = arguments?.getString("judul")
        val nama = arguments?.getString("namaCabang")
        val alamat = arguments?.getString("alamatCabang")
        val nohp = arguments?.getString("nohpCabang")

        tvTambahCabang.text = judul
        etNamaCabang.setText(nama)
        etAlamatCabang.setText(alamat)
        etNohpCabang.setText(nohp)

        if (!tvTambahCabang.text.equals(getString(R.string.tvTambahCabang))) {
            if (judul.equals(getString(R.string.editcabang))) {
                mati()
                bSimpan.text = getString(R.string.sunting)
            }
        } else {
            hidup()
            etNamaCabang.requestFocus()
            bSimpan.text = getString(R.string.simpan)
        }
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
                Toast.makeText(requireContext(), "Data Cabang berhasil diperbarui", Toast.LENGTH_SHORT).show()
                activity?.supportFragmentManager?.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal memperbarui data cabang", Toast.LENGTH_SHORT).show()
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
                    requireContext(),
                    getString(R.string.sukses_simpan_cabang),
                    Toast.LENGTH_SHORT
                ).show()

                // Jika di landscape, clear form
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    etNamaCabang.text.clear()
                    etAlamatCabang.text.clear()
                    etNohpCabang.text.clear()
                    etNamaCabang.requestFocus()
                } else {
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.gagal_simpan_cabang),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
    private fun showToast(messageResId: Int) {
        Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show()
    }
}
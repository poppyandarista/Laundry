package com.luthfiana.laundry.modeldata

data class ModelLaporan(
    var idTransaksi: String = "",
    var namaPelanggan: String? = "",
    var noHpPelanggan: String? = "",
    var namaLayanan: String? = "",
    var hargaLayanan: String? = "",
    var totalTambahan: String? = "",
    var totalBayar: String? = "",
    var metodePembayaran: String? = "",
    var statusPembayaran: String? = "Belum Dibayar",
    var tanggalTransaksi: String? = "",
    var waktuPengambilan: String? = "",
    var noUrut: Int = 0
)
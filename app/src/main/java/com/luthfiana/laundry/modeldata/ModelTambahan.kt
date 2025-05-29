package com.luthfiana.laundry.modeldata

import java.io.Serializable

data class ModelTambahan (
    var idTambahan: String = "",
    var namaLayananTambahan: String = "",
    var hargaTambahan: String = "",
    var namaCabang: String = "",
    var status: String = "",
) : Serializable
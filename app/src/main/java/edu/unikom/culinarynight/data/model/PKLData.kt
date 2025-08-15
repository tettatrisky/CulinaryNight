package edu.unikom.culinarynight.data.model

import com.google.gson.annotations.SerializedName


data class PKLData(
    @SerializedName("kode_provinsi") val kodeProvinsi: Long,
    @SerializedName("nama_provinsi") val namaProvinsi: String,
    @SerializedName("bps_kode_kabupaten_kota") val bpsKodeKabupatenKota: Long,
    @SerializedName("bps_nama_kabupaten_kota") val bpsNamaKabupatenKota: String,
    @SerializedName("lokasi") val lokasi: String,
    @SerializedName("jenis_usaha") val jenisUsaha: String,
    @SerializedName("jumlah_pkl") val jumlahPkl: Long,
    @SerializedName("satuan") val satuan: String,
    @SerializedName("tahun") val tahun: Long,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null
)
package edu.unikom.culinarynight.data.model

import com.google.gson.annotations.SerializedName

data class PKLResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<PKLData>,
    @SerializedName("metadata") val metadata: List<Any>
)

data class PKLData(
    @SerializedName("kode_provinsi") val kodeProvinsi: Long,
    @SerializedName("nama_provinsi") val namaProvinsi: String,
    @SerializedName("bps_kode_kabupaten_kota") val bpsKodeKabupatenKota: Long,
    @SerializedName("bps_nama_kabupaten_kota") val bpsNamaKabupatenKota: String,
    @SerializedName("lokasi") val lokasi: String,
    @SerializedName("jenis_usaha") val jenisUsaha: String,
    @SerializedName("jumlah_pkl") val jumlahPkl: Long,
    @SerializedName("satuan") val satuan: String,
    @SerializedName("tahun") val tahun: Long
)
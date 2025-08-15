package edu.unikom.culinarynight.data.model

data class Voucher(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val discount: Int = 0, // percentage
    val lokasiPkl: String = "",
    val validUntil: Long = 0,
    val isActive: Boolean = true
)
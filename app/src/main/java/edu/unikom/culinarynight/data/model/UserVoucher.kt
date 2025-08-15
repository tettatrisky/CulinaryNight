package edu.unikom.culinarynight.data.model

data class UserVoucher(
    val id: String = "",
    val userId: String = "",
    val voucherId: String = "",
    val voucher: Voucher? = null,
    val claimedAt: Long = System.currentTimeMillis(),
    val isUsed: Boolean = false
)

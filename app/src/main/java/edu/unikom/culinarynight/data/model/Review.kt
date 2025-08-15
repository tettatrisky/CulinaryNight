package edu.unikom.culinarynight.data.model

data class Review(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val lokasiPkl: String = "",
    val rating: Float = 0f,
    val komentar: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
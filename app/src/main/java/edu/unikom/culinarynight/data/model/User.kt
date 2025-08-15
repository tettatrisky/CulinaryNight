package edu.unikom.culinarynight.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
package edu.unikom.culinarynight.data.model

import androidx.compose.ui.graphics.Color

data class CrowdMeter(
    val id: String = "",
    val lokasiPkl: String = "",
    val level: CrowdLevel = CrowdLevel.LOW,
    val lastUpdated: Long = System.currentTimeMillis(),
    val updatedBy: String = ""
)
enum class CrowdLevel(val displayName: String, val color: Color) {
    LOW("Sepi", Color.Green),
    MEDIUM("Sedang", Color.Yellow),
    HIGH("Ramai", Color.Red)
}
package edu.unikom.culinarynight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.unikom.culinarynight.data.model.CrowdLevel
import edu.unikom.culinarynight.data.model.CrowdMeter
import edu.unikom.culinarynight.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// --- PERUBAHAN UTAMA DI SINI ---
// Sekarang ViewModel menerima repository sebagai parameter
class CrowdMeterViewModel(private val repository: FirebaseRepository) : ViewModel() {
    // HAPUS baris ini: private val repository = FirebaseRepository()

    private val _crowdMeter = MutableStateFlow<CrowdMeter?>(null)
    val crowdMeter: StateFlow<CrowdMeter?> = _crowdMeter

    private val _crowdHistory = MutableStateFlow<List<Pair<Long, Int>>>(emptyList())
    val crowdHistory: StateFlow<List<Pair<Long, Int>>> = _crowdHistory

    fun loadCrowdLevel(lokasi: String) {
        viewModelScope.launch {
            repository.getCrowdLevel(lokasi).collect { crowdData ->
                _crowdMeter.value = crowdData
            }
        }
        loadCrowdHistory(lokasi)
    }

    private fun loadCrowdHistory(lokasi: String) {
        viewModelScope.launch {
            repository.getCrowdHistory(lokasi).collect { historyData ->
                _crowdHistory.value = historyData
            }
        }
    }

    fun updateCrowdLevel(
        lokasi: String,
        level: CrowdLevel,
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.updateCrowdLevel(lokasi, level, userId)
            result.fold(
                onSuccess = { onSuccess() },
                onFailure = { onError(it.message ?: "Failed to update crowd level") }
            )
        }
    }
}
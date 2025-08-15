package edu.unikom.culinarynight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.unikom.culinarynight.data.model.CrowdLevel
import edu.unikom.culinarynight.data.model.CrowdMeter
import edu.unikom.culinarynight.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CrowdMeterViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _crowdMeter = MutableStateFlow<CrowdMeter?>(null)
    val crowdMeter: StateFlow<CrowdMeter?> = _crowdMeter

    fun loadCrowdLevel(lokasi: String) {
        viewModelScope.launch {
            repository.getCrowdLevel(lokasi).collect { crowdMeter ->
                _crowdMeter.value = crowdMeter
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
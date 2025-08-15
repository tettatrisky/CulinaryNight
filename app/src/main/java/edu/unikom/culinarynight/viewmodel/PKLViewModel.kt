package edu.unikom.culinarynight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.unikom.culinarynight.data.local.AppDatabase
import edu.unikom.culinarynight.data.model.PKLData
import edu.unikom.culinarynight.data.repository.PKLRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PKLViewModel(private val repo: PKLRepository) : ViewModel() {
    private val _pklData = MutableStateFlow<List<PKLData>>(emptyList())
    val pklData: StateFlow<List<PKLData>> = _pklData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        // collect cached data separately if needed (call site must provide repo.getCachedPKL())
    }

    fun setCachedCollector(cachedFlow: kotlinx.coroutines.flow.Flow<List<PKLData>>) {
        viewModelScope.launch {
            cachedFlow.collect {
                _pklData.value = it
            }
        }
    }

    fun loadPKLData() {
        viewModelScope.launch {
            _isLoading.value = true
            repo.getPKLData().collect { result ->
                _isLoading.value = false
                result.fold(onSuccess = { list ->
                    _pklData.value = list
                    _errorMessage.value = null
                }, onFailure = { e ->
                    _errorMessage.value = e.message
                })
            }
        }
    }

    fun searchPKL(query: String): List<PKLData> {
        val q = query.lowercase()
        return _pklData.value.filter {
            it.lokasi.lowercase().contains(q) || it.jenisUsaha.lowercase().contains(q)
        }
    }
}

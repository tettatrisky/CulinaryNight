package edu.unikom.culinarynight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.unikom.culinarynight.data.model.PKLData
import edu.unikom.culinarynight.data.repository.PKLRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PKLViewModel : ViewModel() {
    private val repository = PKLRepository()

    private val _pklData = MutableStateFlow<List<PKLData>>(emptyList())
    val pklData: StateFlow<List<PKLData>> = _pklData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadPKLData()
    }

    fun loadPKLData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getPKLData().collect { result ->
                result.fold(
                    onSuccess = { data ->
                        _pklData.value = data
                        _errorMessage.value = null
                    },
                    onFailure = { error ->
                        _errorMessage.value = error.message
                    }
                )
                _isLoading.value = false
            }
        }
    }

    fun searchPKL(query: String): List<PKLData> {
        return _pklData.value.filter { pkl ->
            pkl.lokasi.contains(query, ignoreCase = true) ||
                    pkl.jenisUsaha.contains(query, ignoreCase = true)
        }
    }
}

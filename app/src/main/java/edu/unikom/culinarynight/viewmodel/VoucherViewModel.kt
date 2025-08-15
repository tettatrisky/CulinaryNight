package edu.unikom.culinarynight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.unikom.culinarynight.data.model.UserVoucher
import edu.unikom.culinarynight.data.model.Voucher
import edu.unikom.culinarynight.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VoucherViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _availableVouchers = MutableStateFlow<List<Voucher>>(emptyList())
    val availableVouchers: StateFlow<List<Voucher>> = _availableVouchers

    private val _userVouchers = MutableStateFlow<List<UserVoucher>>(emptyList())
    val userVouchers: StateFlow<List<UserVoucher>> = _userVouchers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadAvailableVouchers() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getAvailableVouchers()
            result.fold(
                onSuccess = { vouchers ->
                    _availableVouchers.value = vouchers
                },
                onFailure = { /* Handle error */ }
            )
            _isLoading.value = false
        }
    }

    fun loadUserVouchers(userId: String) {
        viewModelScope.launch {
            val result = repository.getUserVouchers(userId)
            result.fold(
                onSuccess = { vouchers ->
                    _userVouchers.value = vouchers
                },
                onFailure = { /* Handle error */ }
            )
        }
    }

    fun claimVoucher(userId: String, voucherId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.claimVoucher(userId, voucherId)
            result.fold(
                onSuccess = {
                    onSuccess()
                    loadUserVouchers(userId)
                },
                onFailure = { onError(it.message ?: "Failed to claim voucher") }
            )
        }
    }
}

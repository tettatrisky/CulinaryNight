package edu.unikom.culinarynight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.unikom.culinarynight.data.model.Review
import edu.unikom.culinarynight.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadReviews(lokasi: String) {
        viewModelScope.launch {
            repository.getReviewsForLocation(lokasi).collect { reviews ->
                _reviews.value = reviews
            }
        }
    }

    fun addReview(review: Review, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addReview(review)
            result.fold(
                onSuccess = { onSuccess() },
                onFailure = { onError(it.message ?: "Failed to add review") }
            )
            _isLoading.value = false
        }
    }
}
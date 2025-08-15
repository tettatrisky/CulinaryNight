package edu.unikom.culinarynight.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.unikom.culinarynight.data.model.Review
import edu.unikom.culinarynight.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(private val repo: FirebaseRepository = FirebaseRepository()) : ViewModel() {
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    fun loadReviews(lokasi: String) {
        viewModelScope.launch {
            repo.getReviewsForLocation(lokasi).collect { list ->
                _reviews.value = list
            }
        }
    }

    fun addReview(
        review: Review,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                var photoUrl = ""
                if (imageUri != null) {
                    val remotePath = "reviews/${review.userId}/${System.currentTimeMillis()}.jpg"
                    val res = repo.uploadImage(imageUri, remotePath)
                    if (res.isSuccess) photoUrl = res.getOrNull() ?: ""
                    else {
                        onError(res.exceptionOrNull()?.message ?: "Upload failed")
                        return@launch
                    }
                }
                val reviewToSave = review.copy(photoUrl = photoUrl)
                val addRes = repo.addReview(reviewToSave)
                if (addRes.isSuccess) onSuccess() else onError(addRes.exceptionOrNull()?.message ?: "Failed saving review")
            } catch (e: Exception) {
                onError(e.message ?: "Error")
            }
        }
    }
}

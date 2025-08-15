package edu.unikom.culinarynight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.unikom.culinarynight.data.model.AuthState
import edu.unikom.culinarynight.data.model.User
import edu.unikom.culinarynight.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        val currentUser = repository.getCurrentUser()
        if (currentUser != null) {
            _authState.value = AuthState.Authenticated(
                User(
                    id = currentUser.uid,
                    email = currentUser.email ?: "",
                    name = currentUser.displayName ?: ""
                )
            )
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signUp(email, password, name)
            _authState.value = result.fold(
                onSuccess = { AuthState.Authenticated(it) },
                onFailure = { AuthState.Error(it.message ?: "Sign up failed") }
            )
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signIn(email, password)
            _authState.value = result.fold(
                onSuccess = { AuthState.Authenticated(it) },
                onFailure = { AuthState.Error(it.message ?: "Sign in failed") }
            )
        }
    }

    fun signOut() {
        repository.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}
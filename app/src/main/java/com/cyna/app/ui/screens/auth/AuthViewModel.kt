package com.cyna.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyna.app.data.dto.LoginRequest
import com.cyna.app.data.dto.RegisterRequest
import com.cyna.app.data.local.SessionManager
import com.cyna.app.domain.repository.AuthRepository
import dev.kindling.core.components.KToastManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AuthFormState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val fullName: String = "",
    val fullNameError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val acceptTerms: Boolean = false,
    val isLoading: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(AuthFormState())
    val state = _state.asStateFlow()

    fun onEmailChange(v: String) {
        val error = if (v.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(v).matches()) {
            "Invalid email format"
        } else null
        _state.update { it.copy(email = v, emailError = error) }
    }

    fun onPasswordChange(v: String) {
        val error = if (v.isNotEmpty() && v.length < 8) {
            "Password must be at least 8 characters"
        } else null
        _state.update { 
            val newState = it.copy(password = v, passwordError = error)
            if (newState.confirmPassword.isNotEmpty()) {
                val confirmError = if (v != newState.confirmPassword) "Passwords do not match" else null
                newState.copy(confirmPasswordError = confirmError)
            } else newState
        }
    }

    fun onFullNameChange(v: String) {
        val error = if (v.isNotEmpty() && v.trim().isEmpty()) "Name cannot be empty" else null
        _state.update { it.copy(fullName = v, fullNameError = error) }
    }

    fun onConfirmPasswordChange(v: String) {
        val error = if (v.isNotEmpty() && v != _state.value.password) {
            "Passwords do not match"
        } else null
        _state.update { it.copy(confirmPassword = v, confirmPasswordError = error) }
    }

    fun onAcceptTermsChange(v: Boolean) {
        _state.update { it.copy(acceptTerms = v) }
    }

    fun login(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.email.isBlank() || s.password.isBlank() || s.emailError != null || s.passwordError != null) {
            KToastManager.warning("Please fix errors before submitting")
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                authRepository.login(LoginRequest(s.email, s.password))
                KToastManager.success("Welcome back!", "Authentication successful.")
                onSuccess()
            } catch (e: Exception) {
                // Toasts are handled by HttpClient interceptor but we could add specific ones here
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun register(onSuccess: () -> Unit) {
        val s = _state.value
        
        // Final validation check
        val emailError = if (s.email.isBlank()) "Email is required" 
                        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.email).matches()) "Invalid email format"
                        else null
        val passwordError = if (s.password.isBlank()) "Password is required"
                           else if (s.password.length < 8) "Password must be at least 8 characters"
                           else null
        val fullNameError = if (s.fullName.trim().isBlank()) "Full name is required" else null
        val confirmError = if (s.confirmPassword != s.password) "Passwords do not match" else null

        if (emailError != null || passwordError != null || fullNameError != null || confirmError != null) {
            _state.update { it.copy(
                emailError = emailError,
                passwordError = passwordError,
                fullNameError = fullNameError,
                confirmPasswordError = confirmError
            ) }
            KToastManager.warning("Please fix errors before submitting")
            return
        }

        if (!s.acceptTerms) {
            KToastManager.warning("You must accept the terms and conditions")
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                authRepository.register(RegisterRequest(s.fullName, s.email, s.password))
                KToastManager.success("Account created!", "Welcome to Cyna.")
                onSuccess()
            } catch (e: Exception) {
                // Error handling is mostly done by the HttpClient interceptor
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}

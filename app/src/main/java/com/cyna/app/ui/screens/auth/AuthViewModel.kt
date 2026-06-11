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
    val firstName: String = "",
    val firstNameError: String? = null,
    val lastName: String = "",
    val lastNameError: String? = null,
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

    fun onFirstNameChange(v: String) {
        val error = if (v.isNotEmpty() && v.trim().isEmpty()) "First name cannot be empty" else null
        _state.update { it.copy(firstName = v, firstNameError = error) }
    }

    fun onLastNameChange(v: String) {
        val error = if (v.isNotEmpty() && v.trim().isEmpty()) "Last name cannot be empty" else null
        _state.update { it.copy(lastName = v, lastNameError = error) }
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
                // L'appel au repo va déclencher le client Ktor.
                // Les cookies retournés par .NET s'enregistrent dans l'instance HttpCookies.
                authRepository.login(LoginRequest(s.email, s.password))
                KToastManager.success("Welcome back!", "Authentication successful.")
                onSuccess()
            } catch (e: Exception) {
                KToastManager.error("Login failed", e.localizedMessage ?: "Unknown error")
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun register(onSuccess: () -> Unit) {
        val s = _state.value

        val emailError = if (s.email.isBlank()) "Email is required"
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.email).matches()) "Invalid email format"
        else null
        val passwordError = if (s.password.isBlank()) "Password is required"
        else if (s.password.length < 8) "Password must be at least 8 characters"
        else null
        val firstNameError = if (s.firstName.trim().isBlank()) "First name is required" else null
        val lastNameError = if (s.lastName.trim().isBlank()) "Last name is required" else null
        val confirmError = if (s.confirmPassword != s.password) "Passwords do not match" else null

        if (emailError != null || passwordError != null || firstNameError != null || lastNameError != null || confirmError != null) {
            _state.update { it.copy(
                emailError = emailError,
                passwordError = passwordError,
                firstNameError = firstNameError,
                lastNameError = lastNameError,
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
                authRepository.register(RegisterRequest(s.firstName, s.lastName, s.email, s.password))
                KToastManager.success("Account created!", "Welcome to Cyna.")
                onSuccess()
            } catch (e: Exception) {
                KToastManager.error("Registration failed", e.localizedMessage ?: "Unknown error")
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
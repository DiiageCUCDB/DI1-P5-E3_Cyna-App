package com.cyna.app.ui.screens.auth

import android.app.Application
import com.cyna.app.data.dto.LoginRequest
import com.cyna.app.data.dto.RegisterRequest
import com.cyna.app.data.local.SessionManager
import com.cyna.app.domain.repository.AuthRepository
import dev.kindling.compose.KViewModel
import dev.kindling.core.components.KToastManager
import org.koin.core.component.inject

interface AuthContracts {
    data class UiState(
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
}

class AuthViewModel(application: Application) :
    KViewModel<AuthContracts.UiState>(AuthContracts.UiState(), application) {

    private val authRepository: AuthRepository by inject()
    private val sessionManager: SessionManager by inject()

    fun onEmailChange(v: String) {
        val error = if (v.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(v).matches())
            "Invalid email format" else null
        updateState { copy(email = v, emailError = error) }
    }

    fun onPasswordChange(v: String) {
        val error = if (v.isNotEmpty() && v.length < 8)
            "Password must be at least 8 characters" else null
        updateState {
            val updated = copy(password = v, passwordError = error)
            if (confirmPassword.isNotEmpty()) {
                val confirmError = if (v != confirmPassword) "Passwords do not match" else null
                updated.copy(confirmPasswordError = confirmError)
            } else updated
        }
    }

    fun onFullNameChange(v: String) {
        val error = if (v.isNotEmpty() && v.trim().isEmpty()) "Name cannot be empty" else null
        updateState { copy(fullName = v, fullNameError = error) }
    }

    fun onConfirmPasswordChange(v: String) {
        val error = if (v.isNotEmpty() && v != state.value.password)
            "Passwords do not match" else null
        updateState { copy(confirmPassword = v, confirmPasswordError = error) }
    }

    fun onAcceptTermsChange(v: Boolean) = updateState { copy(acceptTerms = v) }

    fun login(onSuccess: () -> Unit) {
        val s = state.value
        if (s.email.isBlank() || s.password.isBlank() || s.emailError != null || s.passwordError != null) {
            KToastManager.warning("Please fix errors before submitting")
            return
        }

        fetchData(
            source = { authRepository.login(LoginRequest(s.email, s.password)) },
            onResult = {
                onSuccess {
                    updateState { copy(isLoading = false) }
                    KToastManager.success("Welcome back!", "Authentication successful.")
                    onSuccess()
                }
                onFailure {
                    updateState { copy(isLoading = false) }
                }
            }
        )
        updateState { copy(isLoading = true) }
    }

    fun register(onSuccess: () -> Unit) {
        val s = state.value

        val emailError = if (s.email.isBlank()) "Email is required"
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.email).matches()) "Invalid email format"
        else null
        val passwordError = if (s.password.isBlank()) "Password is required"
        else if (s.password.length < 8) "Password must be at least 8 characters"
        else null
        val fullNameError = if (s.fullName.trim().isBlank()) "Full name is required" else null
        val confirmError = if (s.confirmPassword != s.password) "Passwords do not match" else null

        if (emailError != null || passwordError != null || fullNameError != null || confirmError != null) {
            updateState { copy(emailError = emailError, passwordError = passwordError, fullNameError = fullNameError, confirmPasswordError = confirmError) }
            KToastManager.warning("Please fix errors before submitting")
            return
        }

        if (!s.acceptTerms) {
            KToastManager.warning("You must accept the terms and conditions")
            return
        }

        val nameParts = s.fullName.trim().split(" ", limit = 2)
        fetchData(
            source = {
                authRepository.register(
                    RegisterRequest(
                        firstName = nameParts[0],
                        lastName  = nameParts.getOrElse(1) { "" },
                        email     = s.email,
                        password  = s.password
                    )
                )
            },
            onResult = {
                onSuccess {
                    updateState { copy(isLoading = false) }
                    KToastManager.success("Account created!", "Welcome to Cyna.")
                    onSuccess()
                }
                onFailure {
                    updateState { copy(isLoading = false) }
                }
            }
        )
        updateState { copy(isLoading = true) }
    }
}
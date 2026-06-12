package com.cyna.app.ui.screens.auth

import android.app.Application
import com.cyna.app.data.dto.LoginRequest
import com.cyna.app.data.dto.RegisterRequest
import com.cyna.app.data.local.SessionManager
import com.cyna.app.domain.repository.AuthRepository
import dev.kindling.compose.KViewModel
import dev.kindling.core.components.KToastManager
import org.koin.core.component.inject

/**
 * Contracts MVI pour les écrans Login et Register (partagent le même ViewModel et le même état).
 */
interface AuthContracts {
    /**
     * État partagé par les écrans Login et Register.
     *
     * Les champs `firstName`/`lastName` ne sont utilisés que par RegisterScreen.
     * Les propriétés `passwordHas*` alimentent l'indicateur de force du mot de passe sur RegisterScreen.
     */
    data class UiState(
        val firstName: String = "",
        val firstNameError: String? = null,
        val lastName: String = "",
        val lastNameError: String? = null,
        val email: String = "",
        val emailError: String? = null,
        val password: String = "",
        val passwordError: String? = null,
        val isLoading: Boolean = false
    ) {
        val passwordHasMinLength get() = password.length >= 8
        val passwordHasUppercase get() = password.any { it.isUpperCase() }
        val passwordHasDigit     get() = password.any { it.isDigit() }
        val passwordHasSpecial   get() = password.any { !it.isLetterOrDigit() }
        val isPasswordStrong     get() = passwordHasMinLength && passwordHasUppercase && passwordHasDigit && passwordHasSpecial
    }
}

/**
 * ViewModel partagé par [LoginScreen] et [RegisterScreen].
 *
 * Délègue login/register à [AuthRepository]. Les erreurs 4xx sont traitées directement
 * dans [com.cyna.app.data.remote.HttpClient] (toast + vibration) — ce ViewModel se contente
 * de remettre `isLoading` à `false` en cas d'échec.
 */
class AuthViewModel(application: Application) :
    KViewModel<AuthContracts.UiState>(AuthContracts.UiState(), application) {

    private val authRepository: AuthRepository by inject()
    private val sessionManager: SessionManager by inject()

    fun onFirstNameChange(v: String) {
        val error = if (v.isNotEmpty() && v.isBlank()) "Prénom invalide" else null
        updateState { copy(firstName = v, firstNameError = error) }
    }

    fun onLastNameChange(v: String) {
        val error = if (v.isNotEmpty() && v.isBlank()) "Nom invalide" else null
        updateState { copy(lastName = v, lastNameError = error) }
    }

    fun onEmailChange(v: String) {
        val error = if (v.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(v).matches())
            "Adresse e-mail invalide" else null
        updateState { copy(email = v, emailError = error) }
    }

    fun onPasswordChange(v: String) {
        updateState { copy(password = v, passwordError = null) }
    }

    /** Déclenche l'appel de connexion. [onSuccess] est appelé si l'API répond 2xx et que le cookie est reçu. */
    fun login(onSuccess: () -> Unit) {
        val s = state.value
        if (s.email.isBlank() || s.password.isBlank() || s.emailError != null) {
            KToastManager.warning("Veuillez corriger les erreurs avant de continuer")
            return
        }

        fetchData(
            source = { authRepository.login(LoginRequest(s.email, s.password)) },
            onResult = {
                onSuccess {
                    updateState { copy(isLoading = false) }
                    KToastManager.success("Bienvenue !", "Authentification réussie.")
                    onSuccess()
                }
                onFailure {
                    updateState { copy(isLoading = false) }
                }
            }
        )
        updateState { copy(isLoading = true) }
    }

    /** Valide les champs puis crée le compte. [onSuccess] navigue vers LoginScreen. */
    fun register(onSuccess: () -> Unit) {
        val s = state.value

        val firstNameError = if (s.firstName.isBlank()) "Le prénom est obligatoire" else null
        val lastNameError  = if (s.lastName.isBlank()) "Le nom est obligatoire" else null
        val emailError     = when {
            s.email.isBlank() -> "L'adresse e-mail est obligatoire"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(s.email).matches() -> "Adresse e-mail invalide"
            else -> null
        }
        val passwordError  = if (!s.isPasswordStrong) "Le mot de passe ne respecte pas les critères" else null

        if (listOfNotNull(firstNameError, lastNameError, emailError, passwordError).isNotEmpty()) {
            updateState { copy(firstNameError = firstNameError, lastNameError = lastNameError, emailError = emailError, passwordError = passwordError) }
            KToastManager.warning("Veuillez corriger les erreurs avant de continuer")
            return
        }

        fetchData(
            source = {
                authRepository.register(
                    RegisterRequest(
                        firstName = s.firstName.trim(),
                        lastName  = s.lastName.trim(),
                        email     = s.email,
                        password  = s.password
                    )
                )
            },
            onResult = {
                onSuccess {
                    updateState { copy(isLoading = false) }
                    KToastManager.success("Compte créé !", "Connectez-vous pour accéder à votre espace.")
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

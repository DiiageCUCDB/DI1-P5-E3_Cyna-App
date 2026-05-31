package com.cyna.app.ui.screens.profile

import android.app.Application
import com.cyna.app.domain.model.Subscription
import com.cyna.app.domain.model.User
import com.cyna.app.domain.repository.UserRepository
import dev.kindling.compose.KViewModel
import dev.kindling.core.components.KToastManager
import org.koin.core.component.inject

interface ProfileContracts {
    data class UiState(
        val user: User? = null,
        val subscriptions: List<Subscription> = emptyList(),
        val loadingUser: Boolean = true,
        val loadingSubs: Boolean = true,
        val nameInput: String = "",
        val emailInput: String = "",
        val emailValid: Boolean = true,
        val savingProfile: Boolean = false,
        val currentPassword: String = "",
        val newPassword: String = "",
        val confirmPassword: String = "",
        val savingPassword: Boolean = false,
        val passwordError: String? = null,
        val cancelTarget: Subscription? = null,
        val cancelling: Boolean = false
    )
}

class ProfileViewModel(application: Application) :
    KViewModel<ProfileContracts.UiState>(ProfileContracts.UiState(), application) {

    private val userRepository: UserRepository by inject()

    init {
        loadUser()
        loadSubscriptions()
    }

    private fun loadUser() {
        fetchData(
            source = { userRepository.getMe() },
            onResult = {
                onSuccess { user ->
                    updateState {
                        copy(
                            user        = user,
                            nameInput   = user.name,
                            emailInput  = user.email,
                            loadingUser = false
                        )
                    }
                }
                onFailure {
                    updateState { copy(loadingUser = false) }
                }
            }
        )
    }

    private fun loadSubscriptions() {
        fetchData(
            source = { userRepository.getSubscriptions() },
            onResult = {
                onSuccess { subs ->
                    updateState { copy(subscriptions = subs, loadingSubs = false) }
                }
                onFailure {
                    updateState { copy(loadingSubs = false) }
                }
            }
        )
    }

    fun onNameChange(v: String)            = updateState { copy(nameInput = v) }
    fun onEmailChange(v: String)           = updateState { copy(emailInput = v) }
    fun onCurrentPasswordChange(v: String) = updateState { copy(currentPassword = v) }
    fun onEmailValidationChange(valid: Boolean) = updateState { copy(emailValid = valid) }
    fun onNewPasswordChange(v: String) = updateState {
        copy(newPassword = v, passwordError = null)
    }
    fun onConfirmPasswordChange(v: String) = updateState {
        copy(confirmPassword = v, passwordError = null)
    }

    fun saveProfile() {
        val s = state.value
        if (!s.emailValid) {
            KToastManager.warning("Please enter a valid email address")
            return
        }
        updateState { copy(savingProfile = true) }
        fetchData(
            source = { userRepository.updateProfile(s.nameInput, s.emailInput) },
            onResult = {
                onSuccess { user ->
                    updateState { copy(savingProfile = false, user = user) }
                    KToastManager.success("Profile updated successfully")
                }
                onFailure { e ->
                    updateState { copy(savingProfile = false) }
                    KToastManager.error("An error occurred", e.message)
                }
            }
        )
    }

    fun savePassword() {
        val s = state.value
        if (s.newPassword != s.confirmPassword) {
            updateState { copy(passwordError = "mismatch") }
            KToastManager.warning("Passwords do not match")
            return
        }
        if (s.newPassword.length < 8) {
            updateState { copy(passwordError = "tooshort") }
            KToastManager.warning("Password must be at least 8 characters")
            return
        }
        updateState { copy(savingPassword = true, passwordError = null) }
        fetchData(
            source = { userRepository.updatePassword(s.currentPassword, s.newPassword) },
            onResult = {
                onSuccess {
                    updateState {
                        copy(
                            savingPassword  = false,
                            currentPassword = "",
                            newPassword     = "",
                            confirmPassword = ""
                        )
                    }
                    KToastManager.success("Password updated successfully")
                }
                onFailure { e ->
                    updateState { copy(savingPassword = false) }
                    KToastManager.error("An error occurred", e.message)
                }
            }
        )
    }

    fun requestCancel(sub: Subscription) = updateState { copy(cancelTarget = sub) }
    fun dismissCancel()                  = updateState { copy(cancelTarget = null) }

    fun confirmCancel() {
        val target = state.value.cancelTarget ?: return
        updateState { copy(cancelling = true) }
        fetchData(
            source = { userRepository.cancelSubscription(target.id) },
            onResult = {
                onSuccess {
                    updateState {
                        copy(
                            cancelling    = false,
                            cancelTarget  = null,
                            subscriptions = subscriptions.filter { it.id != target.id }
                        )
                    }
                    KToastManager.success("Subscription cancelled")
                }
                onFailure { e ->
                    updateState { copy(cancelling = false, cancelTarget = null) }
                    KToastManager.error("An error occurred", e.message)
                }
            }
        )
    }
}
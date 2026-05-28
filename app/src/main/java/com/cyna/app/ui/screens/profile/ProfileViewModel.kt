package com.cyna.app.ui.screens.profile

import android.app.Application
import com.cyna.app.data.dto.*
import com.cyna.app.data.remote.UserAPI
import com.cyna.app.domain.model.Subscription
import com.cyna.app.domain.model.User
import com.cyna.app.ui.core.ViewModel
import org.koin.core.component.inject

// ── State ─────────────────────────────────────────────────────────────────────

data class ProfileState(
    val user: User? = null,
    val subscriptions: List<Subscription> = emptyList(),
    val loadingUser: Boolean = true,
    val loadingSubs: Boolean = true,
    // profile form
    val nameInput: String = "",
    val emailInput: String = "",
    val savingProfile: Boolean = false,
    // password form
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val savingPassword: Boolean = false,
    // cancel dialog
    val cancelTarget: Subscription? = null,
    val cancelling: Boolean = false
)

sealed class ProfileEvent {
    data class Toast(val message: String, val isError: Boolean = false) : ProfileEvent()
    object LoggedOut : ProfileEvent()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class ProfileViewModel(application: Application) : ViewModel<ProfileState>(ProfileState(), application) {

    private val userAPI: UserAPI by inject()

    init {
        loadUser()
        loadSubscriptions()
    }

    private fun loadUser() {
        fetchData(
            source = {
                val dto = userAPI.getMe()
                User(dto.id, dto.name, dto.email, dto.role, dto.isConfirmed)
            },
            onResult = {
                onSuccess { user ->
                    updateState {
                        copy(
                            user = user,
                            nameInput = user.name,
                            emailInput = user.email,
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
            source = {
                userAPI.getSubscriptions()
                    .filter { it.status == "active" }
                    .map { Subscription(it.id, it.productName, it.status, it.duration, it.quantity, it.unitPrice, it.endsAt) }
            },
            onResult = {
                onSuccess { subs -> updateState { copy(subscriptions = subs, loadingSubs = false) } }
                onFailure { updateState { copy(loadingSubs = false) } }
            }
        )
    }

    fun onNameChange(v: String) = updateState { copy(nameInput = v) }
    fun onEmailChange(v: String) = updateState { copy(emailInput = v) }
    fun onCurrentPasswordChange(v: String) = updateState { copy(currentPassword = v) }
    fun onNewPasswordChange(v: String) = updateState { copy(newPassword = v) }
    fun onConfirmPasswordChange(v: String) = updateState { copy(confirmPassword = v) }

    fun saveProfile() {
        val s = state.value
        updateState { copy(savingProfile = true) }
        fetchData(
            source = {
                val dto = userAPI.updateProfile(s.nameInput, s.emailInput)
                User(dto.id, dto.name, dto.email, dto.role, dto.isConfirmed)
            },
            onResult = {
                onSuccess { user ->
                    updateState { copy(savingProfile = false, user = user) }
                    sendEvent(ProfileEvent.Toast("Profile updated successfully"))
                }
                onFailure { e ->
                    updateState { copy(savingProfile = false) }
                    sendEvent(ProfileEvent.Toast(e.message ?: "An error occurred", isError = true))
                }
            }
        )
    }

    fun savePassword() {
        val s = state.value
        if (s.newPassword != s.confirmPassword) {
            sendEvent(ProfileEvent.Toast("Passwords do not match", isError = true))
            return
        }
        if (s.newPassword.length < 8) {
            sendEvent(ProfileEvent.Toast("Password must be at least 8 characters", isError = true))
            return
        }
        updateState { copy(savingPassword = true) }
        fetchData(
            source = { userAPI.updatePassword(s.currentPassword, s.newPassword) },
            onResult = {
                onSuccess {
                    updateState {
                        copy(
                            savingPassword = false,
                            currentPassword = "",
                            newPassword = "",
                            confirmPassword = ""
                        )
                    }
                    sendEvent(ProfileEvent.Toast("Password updated successfully"))
                }
                onFailure { e ->
                    updateState { copy(savingPassword = false) }
                    sendEvent(ProfileEvent.Toast(e.message ?: "An error occurred", isError = true))
                }
            }
        )
    }

    fun requestCancel(sub: Subscription) = updateState { copy(cancelTarget = sub) }
    fun dismissCancel() = updateState { copy(cancelTarget = null) }

    fun confirmCancel() {
        val target = state.value.cancelTarget ?: return
        updateState { copy(cancelling = true) }
        fetchData(
            source = { userAPI.cancelSubscription(target.id) },
            onResult = {
                onSuccess {
                    updateState {
                        copy(
                            cancelling = false,
                            cancelTarget = null,
                            subscriptions = subscriptions.filter { it.id != target.id }
                        )
                    }
                    sendEvent(ProfileEvent.Toast("Subscription cancelled"))
                }
                onFailure { e ->
                    updateState { copy(cancelling = false, cancelTarget = null) }
                    sendEvent(ProfileEvent.Toast(e.message ?: "An error occurred", isError = true))
                }
            }
        )
    }
}
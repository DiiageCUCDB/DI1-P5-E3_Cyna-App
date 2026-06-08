package com.cyna.app.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyna.app.domain.model.Subscription
import androidx.navigation.NavController
import com.cyna.app.ui.core.components.ui.CancelDialog
import com.cyna.app.ui.core.components.ui.FieldMaskWithLabel
import com.cyna.app.ui.core.components.ui.FieldWithLabel
import com.cyna.app.ui.core.components.ui.SectionCard
import com.cyna.app.ui.core.components.ui.profile.SubscriptionRow
import dev.kindling.compose.KScreen
import dev.kindling.core.components.*

// ── Skeleton ─────────────────────────────────────────────────────────────────

@Composable
private fun ProfileSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(3) {
            val cs = MaterialTheme.colorScheme
            Surface(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, cs.outline.copy(.3f)),
                color = cs.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Skeleton(modifier = Modifier.fillMaxWidth(.5f).height(14.dp))
                    Skeleton(modifier = Modifier.fillMaxWidth(.7f).height(11.dp))
                    HorizontalDivider(color = cs.outline.copy(.3f))
                    Skeleton(modifier = Modifier.fillMaxWidth().height(36.dp))
                    Skeleton(modifier = Modifier.fillMaxWidth().height(36.dp))
                }
            }
        }
    }
}

// ── Screen entry ─────────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(navController: NavController) {
    KScreen(
        viewModel = viewModel<ProfileViewModel>(),
        navController = navController
    ) { state, viewModel ->
        ProfileContent(
            state                    = state,
            onFirstNameChange        = viewModel::onFirstNameChange,
            onLastNameChange         = viewModel::onLastNameChange,
            onEmailChange            = viewModel::onEmailChange,
            onEmailValidationChange  = viewModel::onEmailValidationChange,
            onCurrentPasswordChange  = viewModel::onCurrentPasswordChange,
            onNewPasswordChange      = viewModel::onNewPasswordChange,
            onConfirmPasswordChange  = viewModel::onConfirmPasswordChange,
            onSaveProfile            = viewModel::saveProfile,
            onSavePassword           = viewModel::savePassword,
            onCancelRequest          = viewModel::requestCancel,
            onDismissCancel          = viewModel::dismissCancel,
            onConfirmCancel          = viewModel::confirmCancel,
        )
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun ProfileContent(
    state: ProfileContracts.UiState = ProfileContracts.UiState(),
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onEmailValidationChange: (Boolean) -> Unit = {},
    onCurrentPasswordChange: (String) -> Unit = {},
    onNewPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onSaveProfile: () -> Unit = {},
    onSavePassword: () -> Unit = {},
    onCancelRequest: (Subscription) -> Unit = {},
    onDismissCancel: () -> Unit = {},
    onConfirmCancel: () -> Unit = {},
) {
    val cs = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (state.loadingUser) {
            ProfileSkeleton()
        } else {

            // ── Informations personnelles ────────────────────────────────────
            SectionCard(
                title = "Personal Information",
                description = "Manage your contact information and email address."
            ) {
                // Prénom + Nom côte à côte
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FieldWithLabel(
                        label = "First name",
                        value = state.firstNameInput,
                        onValueChange = onFirstNameChange,
                        placeholder = "Jean",
                        modifier = Modifier.weight(1f)
                    )
                    FieldWithLabel(
                        label = "Last name",
                        value = state.lastNameInput,
                        onValueChange = onLastNameChange,
                        placeholder = "Dupont",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Email avec badge "Verified" si isEmailVerified
                FieldMaskWithLabel(
                    label = "Email address",
                    value = state.emailInput,
                    mask = KMaskPattern.Email,
                    onValueChange = onEmailChange,
                    onValidationChange = onEmailValidationChange,
                    trailingContent = if (state.user?.isEmailVerified == true) {
                        {
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF16A34A).copy(.1f)
                            ) {
                                Text(
                                    "Verified",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF16A34A)
                                )
                            }
                        }
                    } else null
                )

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    KButton(
                        onClick = onSaveProfile,
                        isLoading = state.savingProfile,
                        enabled = state.emailValid,
                        size = KButtonSize.Sm
                    ) { Text(if (state.savingProfile) "Saving…" else "Save changes") }
                }
            }

            // ── Sécurité ────────────────────────────────────────────────────
            SectionCard(
                title = "Account Security",
                description = "Update your password to secure access to your services."
            ) {
                FieldWithLabel(
                    "Current password",
                    state.currentPassword,
                    onCurrentPasswordChange,
                    isPassword = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FieldWithLabel(
                        "New password",
                        state.newPassword,
                        onNewPasswordChange,
                        modifier = Modifier.weight(1f),
                        isPassword = true,
                        isError = state.passwordError != null
                    )
                    FieldWithLabel(
                        "Confirm",
                        state.confirmPassword,
                        onConfirmPasswordChange,
                        modifier = Modifier.weight(1f),
                        isPassword = true,
                        isError = state.passwordError == "mismatch"
                    )
                }
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    KButton(
                        onClick = onSavePassword,
                        isLoading = state.savingPassword,
                        variant = KButtonVariant.Outline,
                        size = KButtonSize.Sm
                    ) { Text(if (state.savingPassword) "Updating…" else "Update password") }
                }
            }

            // ── Abonnements ─────────────────────────────────────────────────
            SectionCard(
                title = "Active Subscriptions",
                description = "Manage your current licenses."
            ) {
                if (state.loadingSubs) {
                    repeat(2) {
                        Skeleton(modifier = Modifier.fillMaxWidth().height(60.dp))
                    }
                } else if (state.subscriptions.isEmpty()) {
                    Text(
                        "No active subscriptions at the moment.",
                        fontSize = 12.sp,
                        color = cs.onSurfaceVariant
                    )
                } else {
                    state.subscriptions.forEach { sub ->
                        SubscriptionRow(sub = sub, onCancel = { onCancelRequest(sub) })
                    }
                }
            }
        }
    }

    // Dialog de confirmation de résiliation
    state.cancelTarget?.let { sub ->
        CancelDialog(
            sub = sub,
            cancelling = state.cancelling,
            onDismiss = onDismissCancel,
            onConfirm = onConfirmCancel
        )
    }
}
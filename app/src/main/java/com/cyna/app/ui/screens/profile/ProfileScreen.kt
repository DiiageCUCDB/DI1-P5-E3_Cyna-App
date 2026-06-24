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
import com.cyna.app.ui.core.components.ui.profile.SubscriptionRow
import dev.kindling.compose.KScreen
import dev.kindling.core.components.*

// No "success"/"warning" roles exist in the app's color scheme — used only
// for the "Vérifié" email badge and the unverified-email warning text below.
private val SuccessColor = Color(0xFF16A34A)
private val WarningColor = Color(0xFFD97706)

// ── Skeleton ─────────────────────────────────────────────────────────────────
// Matches web ProfileSkeleton: 3 cards with skeleton rows

@Composable
private fun ProfileSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(3) {
            ProfileSectionCard(title = "", description = "", loadingHeader = true) {}
        }
    }
}

// ── ProfileSectionCard — mirrors shadcn Card with border-b header ─────────────
//
// Matches web:
//   <Card>
//     <CardHeader className="border-b"><CardTitle>…</CardTitle><CardDescription>…</CardDescription></CardHeader>
//     <CardContent className="pt-4">…</CardContent>
//   </Card>

@Composable
private fun ProfileSectionCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    loadingHeader: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = cs.surface,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, cs.outline)
    ) {
        Column {
            // Header with bottom divider
            if (loadingHeader) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Skeleton(modifier = Modifier.fillMaxWidth(0.4f).height(13.dp))
                    Skeleton(modifier = Modifier.fillMaxWidth(0.65f).height(10.dp))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = cs.onSurface
                    )
                    if (description.isNotBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            description,
                            fontSize = 12.sp,
                            color = cs.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
            HorizontalDivider(color = cs.outline, thickness = 1.dp)

            // Content
            if (loadingHeader) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Skeleton(modifier = Modifier.fillMaxWidth().height(34.dp))
                    Skeleton(modifier = Modifier.fillMaxWidth().height(34.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Skeleton(modifier = Modifier.width(100.dp).height(32.dp))
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    content = content
                )
            }
        }
    }
}

// ── Screen entry ─────────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(
    navController: NavController,
    onNavigateTo2FA: () -> Unit = {}
) {
    KScreen(
        viewModel = viewModel<ProfileViewModel>(),
        navController = navController
    ) { state, viewModel ->
        ProfileContent(
            state                   = state,
            onFirstNameChange       = viewModel::onFirstNameChange,
            onLastNameChange        = viewModel::onLastNameChange,
            onEmailChange           = viewModel::onEmailChange,
            onEmailValidationChange = viewModel::onEmailValidationChange,
            onCurrentPasswordChange = viewModel::onCurrentPasswordChange,
            onNewPasswordChange     = viewModel::onNewPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            onSaveProfile           = viewModel::saveProfile,
            onSavePassword          = viewModel::savePassword,
            onCancelRequest         = viewModel::requestCancel,
            onDismissCancel         = viewModel::dismissCancel,
            onConfirmCancel         = viewModel::confirmCancel,
            onNavigateTo2FA         = onNavigateTo2FA
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
    onNavigateTo2FA: () -> Unit = {}
) {
    val cs = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Background follows theme (light/dark)
        Surface(modifier = Modifier.fillMaxSize(), color = cs.background) {}

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Page title
            Text(
                "Mon profil",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = cs.onSurface
            )

            if (state.loadingUser) {
                ProfileSkeleton()
            } else {

                // ── 1. Personal Information ────────────────────────────────────
                //   Matches web: firstName, lastName, email + verified badge + save btn
                ProfileSectionCard(
                    title = "Informations personnelles",
                    description = "Gérez vos coordonnées et votre adresse email."
                ) {
                    // First + Last name row
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FieldWithLabel(
                            label = "Prénom",
                            value = state.firstNameInput,
                            onValueChange = onFirstNameChange,
                            placeholder = "Jean",
                            enabled = !state.savingProfile,
                            modifier = Modifier.weight(1f)
                        )
                        FieldWithLabel(
                            label = "Nom",
                            value = state.lastNameInput,
                            onValueChange = onLastNameChange,
                            placeholder = "Dupont",
                            enabled = !state.savingProfile,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Email + "Verified" badge (matches web)
                    FieldMaskWithLabel(
                        label = "Adresse email",
                        value = state.emailInput,
                        mask = KMaskPattern.Email,
                        onValueChange = onEmailChange,
                        onValidationChange = onEmailValidationChange,
                        enabled = !state.savingProfile,
                        trailingContent = if (state.user?.isEmailVerified == true) {
                            {
                                Spacer(Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SuccessColor.copy(alpha = 0.10f)
                                ) {
                                    Text(
                                        "Vérifié",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = SuccessColor
                                    )
                                }
                            }
                        } else null
                    )

                    // Non-verified warning link
                    if (state.user?.isEmailVerified == false) {
                        Text(
                            "Adresse non vérifiée. Vérifier maintenant",
                            fontSize = 12.sp,
                            color = WarningColor,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Save button — right-aligned
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        KButton(
                            onClick = onSaveProfile,
                            isLoading = state.savingProfile,
                            enabled = state.emailValid && !state.savingProfile,
                            size = KButtonSize.Sm
                        ) { Text(if (state.savingProfile) "Enregistrement…" else "Enregistrer") }
                    }
                }

                // ── 2. Security ────────────────────────────────────────────────
                //   Matches web: currentPassword, newPassword, confirmPassword + optional 2FA row
                ProfileSectionCard(
                    title = "Sécurité",
                    description = "Mettez à jour votre mot de passe pour sécuriser votre accès."
                ) {
                    FieldWithLabel(
                        label = "Mot de passe actuel",
                        value = state.currentPassword,
                        onValueChange = onCurrentPasswordChange,
                        placeholder = "••••••••",
                        isPassword = true,
                        enabled = !state.savingPassword,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FieldWithLabel(
                            label = "Nouveau mot de passe",
                            value = state.newPassword,
                            onValueChange = onNewPasswordChange,
                            isPassword = true,
                            isError = state.passwordError != null,
                            enabled = !state.savingPassword,
                            modifier = Modifier.weight(1f)
                        )
                        FieldWithLabel(
                            label = "Confirmation",
                            value = state.confirmPassword,
                            onValueChange = onConfirmPasswordChange,
                            isPassword = true,
                            isError = state.passwordError == "mismatch",
                            enabled = !state.savingPassword,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        KButton(
                            onClick = onSavePassword,
                            isLoading = state.savingPassword,
                            variant = KButtonVariant.Outline,
                            size = KButtonSize.Sm
                        ) { Text(if (state.savingPassword) "Mise à jour…" else "Mettre à jour") }
                    }

                    // 2FA row (visible only for Admin / SuperAdmin)
                    val role = state.user?.role
                    if (role == "Admin" || role == "SuperAdmin") {
                        HorizontalDivider(color = cs.outline, thickness = 0.5.dp)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = cs.surface,
                            border = BorderStroke(1.dp, cs.outline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        "Double authentification (2FA)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = cs.onSurface
                                    )
                                    Text(
                                        "Requis pour la connexion à l'espace administrateur.",
                                        fontSize = 11.sp,
                                        color = cs.onSurfaceVariant
                                    )
                                }
                                KButton(
                                    text = "Configurer",
                                    onClick = onNavigateTo2FA,
                                    variant = KButtonVariant.Outline,
                                    size = KButtonSize.Sm
                                )
                            }
                        }
                    }
                }

                // ── 3. Subscriptions ───────────────────────────────────────────
                //   Matches web: list of active subscriptions or empty state
                ProfileSectionCard(
                    title = "Abonnements actifs",
                    description = "Gérez vos licences en cours."
                ) {
                    if (state.loadingSubs) {
                        repeat(2) {
                            Skeleton(modifier = Modifier.fillMaxWidth().height(60.dp))
                        }
                    } else if (state.subscriptions.isEmpty()) {
                        Text(
                            "Aucun abonnement actif pour le moment.",
                            fontSize = 12.sp,
                            color = cs.outline
                        )
                    } else {
                        state.subscriptions.forEach { sub ->
                            SubscriptionRow(sub = sub, onCancel = { onCancelRequest(sub) })
                        }
                    }
                }
            }
        }
    }

    // Cancel dialog
    state.cancelTarget?.let { sub ->
        CancelDialog(
            sub = sub,
            cancelling = state.cancelling,
            onDismiss = onDismissCancel,
            onConfirm = onConfirmCancel
        )
    }
}
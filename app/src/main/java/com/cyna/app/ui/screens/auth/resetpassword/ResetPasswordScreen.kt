package com.cyna.app.ui.screens.auth.resetpassword

import dev.kindling.core.components.InputOTP
import dev.kindling.core.components.InputOTPGroup
import dev.kindling.core.components.InputOTPSeparator
import dev.kindling.core.components.InputOTPSlot
import dev.kindling.core.components.KButton
import dev.kindling.core.components.KInput
import dev.kindling.core.components.KLabel
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyna.app.ui.core.components.ui.AuthCard
import dev.kindling.core.components.rememberInputOTPState

// No "success" role exists in the app's color scheme — used only for the
// reset-confirmed checkmark badge and "rule met" icon/text below.
private val SuccessBg = Color(0xFFDCFCE7)
private val SuccessIcon = Color(0xFF16A34A)
private val SuccessGreen = Color(0xFF22C55E)
private val SuccessGreenText = Color(0xFF15803D)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    initialEmail: String = "",
    onNavigateToLogin: () -> Unit
) {
    val viewModel: ResetPasswordViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(initialEmail) { viewModel.initEmail(initialEmail) }

    ResetPasswordContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onCodeChange = viewModel::onCodeChange,
        onPasswordChange = viewModel::onPasswordChange,
        onPasswordFocus = viewModel::onPasswordFocus,
        onSubmit = viewModel::submit,
        onNavigateToLogin = onNavigateToLogin
    )
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun ResetPasswordContent(
    state: ResetPasswordContracts.UiState = ResetPasswordContracts.UiState(),
    onEmailChange: (String) -> Unit = {},
    onCodeChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onPasswordFocus: () -> Unit = {},
    onSubmit: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val cs = MaterialTheme.colorScheme
    val otpState = rememberInputOTPState(
        value = state.code,
        length = 6,
        onValueChange = onCodeChange
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        contentAlignment = Alignment.Center
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = cs.background) {}

        AnimatedContent(
            targetState = state.success,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "reset-anim"
        ) { success ->
            if (success) {
                // ── Success ───────────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AuthCard {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .align(Alignment.CenterHorizontally),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = SuccessBg
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = SuccessIcon,
                                    modifier = Modifier
                                        .padding(14.dp)
                                        .size(28.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            "Mot de passe réinitialisé",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = cs.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Votre mot de passe a été mis à jour. Vous pouvez maintenant vous connecter.",
                            fontSize = 14.sp,
                            color = cs.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(28.dp))

                        KButton(
                            text = "Se connecter",
                            onClick = onNavigateToLogin,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                // ── Form ─────────────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AuthCard {
                        // Key icon
                        Box(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = cs.primaryContainer
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = cs.onPrimaryContainer,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .size(24.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            "Nouveau mot de passe",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = cs.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Saisissez le code reçu par email et choisissez un nouveau mot de passe.",
                            fontSize = 14.sp,
                            color = cs.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(24.dp))

                        // Error banner
                        AnimatedVisibility(visible = state.error != null) {
                            Column {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = cs.errorContainer,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp, cs.error.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        state.error ?: "",
                                        fontSize = 13.sp,
                                        color = cs.error,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                            }
                        }

                        // Email
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            KLabel("Email")
                            KInput(
                                value = state.email,
                                onValueChange = onEmailChange,
                                placeholder = "vous@example.com",
                                enabled = !state.isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        // OTP — 3+3 with separator
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            KLabel(
                                "Code à 6 chiffres",
                                modifier = Modifier.fillMaxWidth(),
                            )
                            InputOTP(state = otpState, enabled = !state.isLoading) {
                                InputOTPGroup {
                                    InputOTPSlot(otpState, 0, isFirst = true)
                                    InputOTPSlot(otpState, 1)
                                    InputOTPSlot(otpState, 2, isLast = true)
                                }
                                InputOTPSeparator()
                                InputOTPGroup {
                                    InputOTPSlot(otpState, 3, isFirst = true)
                                    InputOTPSlot(otpState, 4)
                                    InputOTPSlot(otpState, 5, isLast = true)
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Le code expire dans 15 minutes. ",
                                    fontSize = 12.sp,
                                    color = cs.outline
                                )
                                TextButton(
                                    onClick = { /* navigate to forgot-password */ },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        "Renvoyer un code",
                                        fontSize = 12.sp,
                                        color = cs.primary,
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // New password
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            KLabel("Nouveau mot de passe")
                            KInput(
                                value = state.password,
                                onValueChange = onPasswordChange,
                                placeholder = "••••••••",
                                isPassword = true,
                                enabled = !state.isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Password strength rules (visible when password non-empty)
                        AnimatedVisibility(visible = state.showRules && state.password.isNotEmpty()) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = cs.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        state.rules.forEach { (rule, ok) ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (ok) Icons.Default.Check else Icons.Default.Circle,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp),
                                                    tint = if (ok) SuccessGreen else cs.outline
                                                )
                                                Text(
                                                    rule.label,
                                                    fontSize = 12.sp,
                                                    color = if (ok) SuccessGreenText else cs.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        KButton(
                            text = if (state.isLoading) "Réinitialisation…" else "Réinitialiser le mot de passe",
                            onClick = onSubmit,
                            isLoading = state.isLoading,
                            enabled = state.isReady && !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    TextButton(onClick = onNavigateToLogin) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = cs.outline,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Retour à la connexion",
                            fontSize = 14.sp,
                            color = cs.outline
                        )
                    }
                }
            }
        }
    }
}
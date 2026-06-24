package com.cyna.app.ui.screens.auth.forgotpassword


import dev.kindling.core.components.KButton
import dev.kindling.core.components.KInput
import dev.kindling.core.components.KLabel
import androidx.navigation.NavController
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyna.app.ui.core.components.ui.AuthCard

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    onNavigateToResetPassword: (String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val viewModel: ForgotPasswordViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    ForgotPasswordContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onSubmit = viewModel::submit,
        onResend = viewModel::resend,
        onContinueToReset = { onNavigateToResetPassword(state.email) },
        onBack = onNavigateToLogin
    )
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun ForgotPasswordContent(
    state: ForgotPasswordContracts.UiState = ForgotPasswordContracts.UiState(),
    onEmailChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onResend: () -> Unit = {},
    onContinueToReset: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val cs = MaterialTheme.colorScheme

    // Background follows theme (light/dark)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = cs.background
        ) {}

        AnimatedContent(
            targetState = state.submitted,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "forgot-anim"
        ) { submitted ->
            if (submitted) {
                // ── Success state ─────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AuthCard {
                        // Mail icon in primary-tinted circle
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .align(Alignment.CenterHorizontally),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = cs.primaryContainer
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mail,
                                    contentDescription = null,
                                    tint = cs.onPrimaryContainer,
                                    modifier = Modifier
                                        .padding(14.dp)
                                        .size(28.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            "Vérifiez votre boîte mail",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = cs.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            buildString {
                                append("Si ")
                                append(state.email)
                                append(" correspond à un compte, vous recevrez un code à 6 chiffres valable ")
                            },
                            fontSize = 14.sp,
                            color = cs.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "15 minutes.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = cs.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            "Pensez à vérifier vos spams.",
                            fontSize = 12.sp,
                            color = cs.outline,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(28.dp))

                        // "Saisir le code reçu →" button
                        KButton(
                            text = "Saisir le code reçu →",
                            onClick = onContinueToReset,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        // Resend link
                        TextButton(
                            onClick = onResend,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                "Renvoyer un code",
                                fontSize = 12.sp,
                                color = cs.outline,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                            )
                        }
                    }
                }
            } else {
                // ── Form state ────────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AuthCard {
                        // Shield icon
                        Box(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = cs.primaryContainer
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
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
                            "Mot de passe oublié ?",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = cs.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Saisissez votre email et nous vous enverrons un code de réinitialisation.",
                            fontSize = 14.sp,
                            color = cs.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(28.dp))

                        // Error banner
                        AnimatedVisibility(visible = state.error != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = cs.errorContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        state.error ?: "",
                                        fontSize = 13.sp,
                                        color = cs.error
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                        }

                        // Email field
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            KLabel("Adresse email")
                            KInput(
                                value = state.email,
                                onValueChange = onEmailChange,
                                placeholder = "vous@example.com",
                                enabled = !state.isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        KButton(
                            text = if (state.isLoading) "Envoi en cours…" else "Envoyer le code",
                            onClick = onSubmit,
                            isLoading = state.isLoading,
                            enabled = state.isEmailValid && !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Back to login link
                    TextButton(onClick = onBack) {
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
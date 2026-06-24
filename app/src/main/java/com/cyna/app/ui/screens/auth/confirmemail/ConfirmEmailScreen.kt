package com.cyna.app.ui.screens.auth.confirmemail

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
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
import dev.kindling.core.components.InputOTP
import dev.kindling.core.components.InputOTPGroup
import dev.kindling.core.components.InputOTPSeparator
import dev.kindling.core.components.InputOTPSlot
import dev.kindling.core.components.KButton
import dev.kindling.core.components.KInput
import dev.kindling.core.components.KLabel
import dev.kindling.core.components.rememberInputOTPState

// No "success" role exists in the app's color scheme — used only for the
// email-confirmed checkmark badge below.
private val SuccessBg = Color(0xFFDCFCE7)
private val SuccessIcon = Color(0xFF16A34A)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun ConfirmEmailScreen(
    navController: NavController,
    initialEmail: String = "",
    onNavigateToLogin: () -> Unit
) {
    val viewModel: ConfirmEmailViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(initialEmail) { viewModel.initEmail(initialEmail) }

    ConfirmEmailContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onCodeChange = viewModel::onCodeChange,
        onSubmit = viewModel::submit,
        onResend = viewModel::resend,
        onBack = onNavigateToLogin
    )
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun ConfirmEmailContent(
    state: ConfirmEmailContracts.UiState = ConfirmEmailContracts.UiState(),
    onEmailChange: (String) -> Unit = {},
    onCodeChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onResend: () -> Unit = {},
    onBack: () -> Unit = {}
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
            label = "confirm-anim"
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
                            "Email confirmé !",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = cs.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Votre adresse email a été vérifiée avec succès.",
                            fontSize = 14.sp,
                            color = cs.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(28.dp))

                        KButton(
                            text = "Continuer vers l'accueil",
                            onClick = onBack,
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
                        // Mail check icon
                        Box(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = cs.primaryContainer
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
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
                            "Vérifiez votre email",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = cs.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            buildString {
                                append("Un code à 6 chiffres a été envoyé à votre adresse email. ")
                                append("Il expire dans ")
                            },
                            fontSize = 14.sp,
                            color = cs.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "30 minutes.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
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

                        // Email field (editable)
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

                        // OTP 3+3
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            KLabel(
                                "Code de vérification",
                                modifier = Modifier.fillMaxWidth()
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
                        }

                        Spacer(Modifier.height(24.dp))

                        KButton(
                            text = if (state.isLoading) "Vérification…" else "Confirmer mon email",
                            onClick = onSubmit,
                            isLoading = state.isLoading,
                            enabled = state.isReady && !state.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Vous n'avez pas reçu de code ? ",
                                fontSize = 12.sp,
                                color = cs.outline
                            )
                            TextButton(
                                onClick = onResend,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    "Renvoyer",
                                    fontSize = 12.sp,
                                    color = cs.primary,
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

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
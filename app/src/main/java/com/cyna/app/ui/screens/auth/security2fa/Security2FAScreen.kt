package com.cyna.app.ui.screens.auth.security2fa

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.cyna.app.ui.core.components.ui.AuthCard
import dev.kindling.core.components.InputOTP
import dev.kindling.core.components.InputOTPGroup
import dev.kindling.core.components.InputOTPSeparator
import dev.kindling.core.components.InputOTPSlot
import dev.kindling.core.components.KButton
import dev.kindling.core.components.KButtonVariant
import dev.kindling.core.components.rememberInputOTPState

// No "success" role exists in the app's color scheme — used only for the
// activation-confirmed checkmark badge below.
private val SuccessBg = Color(0xFFDCFCE7)
private val SuccessIcon = Color(0xFF16A34A)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun Security2FAScreen(
    navController: NavController,
    onNavigateToAdmin: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val viewModel: Security2FAViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    Security2FAContent(
        state = state,
        onCodeChange = viewModel::onCodeChange,
        onConfirm = viewModel::confirm,
        onNavigateToAdmin = onNavigateToAdmin,
        onNavigateToProfile = onNavigateToProfile
    )
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun Security2FAContent(
    state: Security2FAContracts.UiState = Security2FAContracts.UiState(),
    onCodeChange: (String) -> Unit = {},
    onConfirm: () -> Unit = {},
    onNavigateToAdmin: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
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
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = cs.background) {}

        AnimatedContent(
            targetState = state.activated,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "2fa-anim"
        ) { activated ->
            if (activated) {
                // ── Activated success card ────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(16.dp))
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

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "2FA activé avec succès",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = cs.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Vous devrez désormais utiliser la connexion administrateur avec votre code TOTP.",
                            fontSize = 13.sp,
                            color = cs.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(24.dp))

                        KButton(
                            text = "Accéder au tableau de bord",
                            onClick = onNavigateToAdmin,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        KButton(
                            text = "Retour au profil",
                            onClick = onNavigateToProfile,
                            variant = KButtonVariant.Outline,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                // ── Setup flow ────────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = cs.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = cs.onPrimaryContainer,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                "Double authentification",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = cs.onSurface
                            )
                            Text(
                                "Sécurisez votre compte administrateur avec un code TOTP.",
                                fontSize = 12.sp,
                                color = cs.onSurfaceVariant
                            )
                        }
                    }

                    // Step 1 — QR code card
                    Step1Card(state = state)

                    // Step 2 — Confirm card (shown only once setup loaded)
                    if (!state.loadingSetup && state.setup != null) {
                        Step2Card(
                            otpState = otpState,
                            state = state,
                            onConfirm = onConfirm
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Step1Card(state: Security2FAContracts.UiState) {
    val cs = MaterialTheme.colorScheme
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = cs.surface,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, cs.outlineVariant)
    ) {
        Column {
            // Header
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text("1. Scannez le QR code", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
                Spacer(Modifier.height(2.dp))
                Text(
                    "Avec Google Authenticator, Authy, ou toute autre application TOTP.",
                    fontSize = 12.sp, color = cs.onSurfaceVariant
                )
            }
            HorizontalDivider(color = cs.outlineVariant, thickness = 0.5.dp)

            Column(modifier = Modifier.padding(16.dp)) {
                when {
                    state.loadingSetup -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = cs.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    state.setupError != null && state.setup == null -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = cs.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                state.setupError,
                                fontSize = 13.sp,
                                color = cs.error,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    state.setup != null -> {
                        val qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&margin=8&data=" +
                                java.net.URLEncoder.encode(state.setup.otpAuthUrl, "UTF-8")

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // QR code image
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp, cs.outline
                                )
                            ) {
                                AsyncImage(
                                    model = qrUrl,
                                    contentDescription = "QR code 2FA",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(120.dp)
                                )
                            }

                            // Secret key + instructions
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Vous ne pouvez pas scanner ? Entrez cette clé manuellement :",
                                    fontSize = 12.sp,
                                    color = cs.onSurfaceVariant
                                )

                                // Secret key with copy button
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = cs.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            state.setup.secret,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = cs.onSurfaceVariant,
                                            letterSpacing = 1.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(state.setup.secret))
                                                copied = true
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                                                contentDescription = "Copier",
                                                tint = if (copied) SuccessIcon else cs.onSurfaceVariant,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Smartphone,
                                        contentDescription = null,
                                        tint = cs.outline,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        "Type : Basé sur le temps (TOTP), 6 chiffres, 30 secondes.",
                                        fontSize = 11.sp,
                                        color = cs.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Step2Card(
    otpState: dev.kindling.core.components.InputOTPState,
    state: Security2FAContracts.UiState,
    onConfirm: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val isReady = state.code.trim().length == 6

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = cs.surface,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, cs.outlineVariant)
    ) {
        Column {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text("2. Confirmez l'activation", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
                Spacer(Modifier.height(2.dp))
                Text(
                    "Saisissez le code à 6 chiffres affiché dans votre application.",
                    fontSize = 12.sp, color = cs.onSurfaceVariant
                )
            }
            HorizontalDivider(color = cs.outlineVariant, thickness = 0.5.dp)

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Error
                AnimatedVisibility(visible = state.confirmError != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = cs.errorContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, cs.error.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            state.confirmError ?: "",
                            fontSize = 13.sp,
                            color = cs.error,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // OTP
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InputOTP(state = otpState, enabled = !state.confirming) {
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

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    KButton(
                        text = if (state.confirming) "Activation…" else "Activer le 2FA",
                        onClick = onConfirm,
                        isLoading = state.confirming,
                        enabled = isReady && !state.confirming,
                        modifier = Modifier.widthIn(min = 180.dp)
                    )
                }
            }
        }
    }
}
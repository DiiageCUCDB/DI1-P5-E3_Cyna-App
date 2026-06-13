package com.cyna.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyna.app.R
import com.cyna.app.ui.core.components.ui.FieldWithLabel
import com.cyna.app.ui.core.components.ui.KLink
import com.cyna.app.ui.core.components.ui.layout.MainScaffold
import dev.kindling.compose.KScreen
import dev.kindling.core.components.KButton

@Composable
fun LoginScreen(
    navController: NavController,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
) {
    KScreen(
        viewModel = viewModel<AuthViewModel>(),
        navController = navController
    ) { state, vm ->
        LoginContent(
            state = state,
            onEmailChange = vm::onEmailChange,
            onPasswordChange = vm::onPasswordChange,
            onLogin = { vm.login(onLoginSuccess) },
            onNavigateToRegister = onNavigateToRegister
        )
    }
}

@Composable
private fun LoginContent(
    state: AuthContracts.UiState = AuthContracts.UiState(),
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    var rememberMe by remember { mutableStateOf(false) }

    MainScaffold(showLayout = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 450.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Lock icon
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = stringResource(R.string.login_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    // Subtitle with register link
                    Row(
                        modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.login_subtitle_prefix),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        KLink(
                            text = stringResource(R.string.login_subtitle_link),
                            onClick = onNavigateToRegister
                        )
                    }

                    // Email
                    FieldWithLabel(
                        label = stringResource(R.string.login_email_label),
                        value = state.email,
                        onValueChange = onEmailChange,
                        placeholder = stringResource(R.string.login_email_placeholder),
                        isError = state.emailError != null,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.emailError != null) {
                        Text(
                            text = state.emailError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.align(Alignment.Start).padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password
                    FieldWithLabel(
                        label = stringResource(R.string.login_password_label),
                        value = state.password,
                        onValueChange = onPasswordChange,
                        placeholder = stringResource(R.string.login_password_placeholder),
                        isPassword = true,
                        isError = state.passwordError != null,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.passwordError != null) {
                        Text(
                            text = state.passwordError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.align(Alignment.Start).padding(top = 4.dp)
                        )
                    }

                    // Remember me + forgot password
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                enabled = !state.isLoading
                            )
                            Text(
                                text = stringResource(R.string.login_remember_me),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        KLink(
                            text = stringResource(R.string.login_forgot_password),
                            onClick = {}
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    KButton(
                        text = stringResource(R.string.login_button),
                        onClick = onLogin,
                        isLoading = state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

package com.cyna.app.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.cyna.app.R
import com.cyna.app.ui.core.components.ui.FieldWithLabel
import com.cyna.app.ui.core.components.ui.KLink
import com.cyna.app.ui.core.components.ui.layout.MainScaffold
import dev.kindling.core.components.KButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    MainScaffold(showLayout = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
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
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.padding(bottom = 16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = stringResource(R.string.login_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = stringResource(R.string.login_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                    )

                    FieldWithLabel(
                        label = stringResource(R.string.login_email_label),
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        placeholder = stringResource(R.string.login_email_placeholder),
                        isError = state.emailError != null,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.emailError != null) {
                        Text(
                            text = state.emailError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FieldWithLabel(
                        label = stringResource(R.string.login_password_label),
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
                        placeholder = stringResource(R.string.login_password_placeholder),
                        isPassword = true,
                        isError = state.passwordError != null,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.passwordError != null) {
                        Text(
                            text = state.passwordError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(top = 4.dp)
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        KLink(
                            text = stringResource(R.string.login_forgot_password),
                            onClick = { /* Mock forgot password */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    KButton(
                        text = stringResource(R.string.login_button),
                        onClick = { viewModel.login(onLoginSuccess) },
                        isLoading = state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.login_no_account) + " ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.register_button),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable(onClick = onNavigateToRegister)
                        )
                    }
                }
            }
        }
    }
}
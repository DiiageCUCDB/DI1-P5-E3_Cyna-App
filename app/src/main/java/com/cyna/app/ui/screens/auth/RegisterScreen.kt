package com.cyna.app.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.cyna.app.ui.core.components.ui.layout.MainScaffold
import dev.kindling.core.components.KButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
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
                    Text(
                        text = stringResource(R.string.register_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = stringResource(R.string.register_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                    )

                    FieldWithLabel(
                        label = stringResource(R.string.register_name_label),
                        value = state.fullName,
                        onValueChange = viewModel::onFullNameChange,
                        placeholder = stringResource(R.string.register_name_placeholder),
                        isError = state.fullNameError != null,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.fullNameError != null) {
                        Text(
                            text = state.fullNameError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FieldWithLabel(
                        label = stringResource(R.string.register_email_label),
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        placeholder = stringResource(R.string.register_email_placeholder),
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
                        label = stringResource(R.string.register_password_label),
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

                    Spacer(modifier = Modifier.height(16.dp))

                    FieldWithLabel(
                        label = stringResource(R.string.register_confirm_password_label),
                        value = state.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        placeholder = stringResource(R.string.login_password_placeholder),
                        isPassword = true,
                        isError = state.confirmPasswordError != null,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.confirmPasswordError != null) {
                        Text(
                            text = state.confirmPasswordError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(top = 4.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Checkbox(
                            checked = state.acceptTerms,
                            onCheckedChange = viewModel::onAcceptTermsChange,
                            enabled = !state.isLoading
                        )
                        Text(
                            text = stringResource(R.string.register_terms_accept),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    KButton(
                        text = stringResource(R.string.register_button),
                        onClick = { viewModel.register(onRegisterSuccess) },
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
                            text = stringResource(R.string.register_has_account).split("?").first() + "? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.login_button),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable(onClick = onNavigateToLogin)
                        )
                    }
                }
            }
        }
    }
}

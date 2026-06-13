package com.cyna.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
fun RegisterScreen(
    navController: NavController,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
) {
    KScreen(
        viewModel = viewModel<AuthViewModel>(),
        navController = navController
    ) { state, vm ->
        RegisterContent(
            state = state,
            onFirstNameChange = vm::onFirstNameChange,
            onLastNameChange = vm::onLastNameChange,
            onEmailChange = vm::onEmailChange,
            onPasswordChange = vm::onPasswordChange,
            onRegister = { vm.register(onRegisterSuccess) },
            onNavigateToLogin = onNavigateToLogin
        )
    }
}

@Composable
private fun RegisterContent(
    state: AuthContracts.UiState = AuthContracts.UiState(),
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onRegister: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
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
                    .widthIn(max = 500.dp)
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

                    // Subtitle with login link
                    Row(
                        modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.register_subtitle_prefix),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        KLink(
                            text = stringResource(R.string.register_subtitle_link),
                            onClick = onNavigateToLogin
                        )
                    }

                    // First name + Last name side by side
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            FieldWithLabel(
                                label = stringResource(R.string.register_first_name_label),
                                value = state.firstName,
                                onValueChange = onFirstNameChange,
                                placeholder = stringResource(R.string.register_first_name_placeholder),
                                isError = state.firstNameError != null,
                                enabled = !state.isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (state.firstNameError != null) {
                                Text(
                                    text = state.firstNameError,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            FieldWithLabel(
                                label = stringResource(R.string.register_last_name_label),
                                value = state.lastName,
                                onValueChange = onLastNameChange,
                                placeholder = stringResource(R.string.register_last_name_placeholder),
                                isError = state.lastNameError != null,
                                enabled = !state.isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (state.lastNameError != null) {
                                Text(
                                    text = state.lastNameError,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email
                    FieldWithLabel(
                        label = stringResource(R.string.register_email_label),
                        value = state.email,
                        onValueChange = onEmailChange,
                        placeholder = stringResource(R.string.register_email_placeholder),
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
                        label = stringResource(R.string.register_password_label),
                        value = state.password,
                        onValueChange = onPasswordChange,
                        placeholder = stringResource(R.string.login_password_placeholder),
                        isPassword = true,
                        isError = state.passwordError != null,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Password criteria checklist (visible dès qu'on tape)
                    if (state.password.isNotEmpty()) {
                        PasswordCriteria(state = state)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    KButton(
                        text = stringResource(R.string.register_button),
                        onClick = onRegister,
                        isLoading = state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // "Étape suivante" info box
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.register_next_step_title),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.register_next_step_body),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordCriteria(state: AuthContracts.UiState) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            CriterionRow(stringResource(R.string.password_criteria_length), state.passwordHasMinLength)
            CriterionRow(stringResource(R.string.password_criteria_uppercase), state.passwordHasUppercase)
            CriterionRow(stringResource(R.string.password_criteria_digit), state.passwordHasDigit)
            CriterionRow(stringResource(R.string.password_criteria_special), state.passwordHasSpecial)
        }
    }
}

@Composable
private fun CriterionRow(label: String, met: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(
            imageVector = if (met) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (met) MaterialTheme.colorScheme.primary
                   else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (met) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

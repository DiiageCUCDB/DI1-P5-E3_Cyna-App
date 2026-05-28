package com.cyna.app.ui.screens.profile

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.cyna.app.domain.model.Subscription
import com.cyna.app.domain.model.User
import dev.kindling.core.components.*
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// ── helpers ───────────────────────────────────────────────────────────────────

private fun initials(name: String) =
    name.trim().split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")

private fun formatDate(iso: String): String = runCatching {
    val inst = Instant.parse(iso)
    DateTimeFormatter.ofPattern("MMM d, yyyy").withLocale(Locale.getDefault())
        .format(inst.atZone(ZoneId.systemDefault()))
}.getOrDefault(iso)

private fun formatPrice(price: Double): String =
    NumberFormat.getCurrencyInstance(Locale.getDefault()).format(price)

// ── Section card ─────────────────────────────────────────────────────────────

@Composable
private fun SectionCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = cs.surface,
        tonalElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, cs.outline.copy(.3f))
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
                Spacer(Modifier.height(2.dp))
                Text(description, fontSize = 12.sp, color = cs.onSurfaceVariant, lineHeight = 18.sp)
            }
            HorizontalDivider(color = cs.outline.copy(.3f), thickness = 0.5.dp)
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content
            )
        }
    }
}

// ── Labeled field ─────────────────────────────────────────────────────────────

@Composable
private fun FieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        KLabel(label)
        if (trailingContent != null) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                KInput(
                    value = value,
                    onValueChange = onValueChange,
                    isPassword = isPassword,
                    modifier = Modifier.weight(1f)
                )
                trailingContent()
            }
        } else {
            KInput(value = value, onValueChange = onValueChange, isPassword = isPassword)
        }
    }
}

// ── Cancel dialog ─────────────────────────────────────────────────────────────

@Composable
private fun CancelDialog(
    sub: Subscription,
    cancelling: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    DialogContent(open = true, onDismiss = onDismiss) {
        DialogHeader {
            DialogTitle("Cancel Subscription")
            DialogDescription("Are you sure you want to cancel this subscription? This action cannot be undone.")
        }
        Spacer(Modifier.height(12.dp))
        // Subscription info box
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = cs.error.copy(.07f),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, cs.error.copy(.25f), RoundedCornerShape(8.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(sub.productName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
                Spacer(Modifier.height(2.dp))
                Text(
                    "${sub.quantity} user${if (sub.quantity != 1) "s" else ""} · Renews ${formatDate(sub.endsAt)}",
                    fontSize = 11.sp, color = cs.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        DialogFooter {
            KButton("Keep", onClick = onDismiss, variant = KButtonVariant.Outline, size = KButtonSize.Sm)
            KButton(
                onClick = onConfirm,
                variant = KButtonVariant.Destructive,
                size = KButtonSize.Sm,
                isLoading = cancelling
            ) { Text("Cancel Subscription") }
        }
    }
}

// ── Subscription item ─────────────────────────────────────────────────────────

@Composable
private fun SubscriptionRow(sub: Subscription, onCancel: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = cs.primary.copy(.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, cs.primary.copy(.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(sub.productName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
                Spacer(Modifier.height(2.dp))
                Text(
                    "${sub.quantity} users · Renews ${formatDate(sub.endsAt)}",
                    fontSize = 11.sp, color = cs.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            KButton("Cancel", onClick = onCancel, variant = KButtonVariant.Destructive, size = KButtonSize.Sm)
        }
    }
}

// ── Skeleton ─────────────────────────────────────────────────────────────────

@Composable
private fun ProfileSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(3) {
            val cs = MaterialTheme.colorScheme
            Surface(
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, cs.outline.copy(.3f)),
                color = cs.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

// ── Avatar ────────────────────────────────────────────────────────────────────

@Composable
private fun UserAvatar(user: User) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(cs.primary.copy(.15f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials(user.name),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = cs.primary
        )
    }
}

// ── Nav item ─────────────────────────────────────────────────────────────────

@Composable
private fun NavItem(icon: ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isActive) cs.primary.copy(.1f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon, null,
                modifier = Modifier.size(16.dp),
                tint = if (isActive) cs.primary else cs.onSurfaceVariant
            )
            Text(
                label,
                fontSize = 12.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isActive) cs.primary else cs.onSurfaceVariant
            )
        }
    }
}

// ── Main screen ───────────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(
    onNavigateToCatalog: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    vm: ProfileViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is ProfileEvent.Toast -> snackbarHostState.showSnackbar(event.message)
                is ProfileEvent.LoggedOut -> { /* navigate */ }
            }
        }
    }

    val cs = MaterialTheme.colorScheme

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Top bar ──────────────────────────────────────────────────────
            Surface(color = cs.surface, tonalElevation = 1.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Account", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
                    KButton(
                        onClick = {},
                        variant = KButtonVariant.Ghost,
                        size = KButtonSize.IconSm
                    ) {
                        Icon(Icons.Default.Settings, null, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                // ── User header ──────────────────────────────────────────────
                if (!state.loadingUser && state.user != null) {
                    val user = state.user!!
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        UserAvatar(user)
                        Column {
                            Text(user.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
                            Text(user.email, fontSize = 12.sp, color = cs.onSurfaceVariant)
                        }
                    }
                }

                // ── Sidebar nav ──────────────────────────────────────────────
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = cs.surfaceVariant.copy(.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        NavItem(Icons.Default.Person, "Profile & Security", isActive = true, onClick = {})
                        NavItem(Icons.Default.Receipt, "Billing & Payment", isActive = false, onClick = onNavigateToOrders)
                    }
                }

                // ── Content ──────────────────────────────────────────────────
                if (state.loadingUser) {
                    ProfileSkeleton()
                } else {

                    // Personal Info
                    SectionCard(
                        title = "Personal Information",
                        description = "Manage your contact information and email address."
                    ) {
                        FieldWithLabel(
                            label = "Full name",
                            value = state.nameInput,
                            onValueChange = vm::onNameChange
                        )
                        FieldWithLabel(
                            label = "Email address",
                            value = state.emailInput,
                            onValueChange = vm::onEmailChange,
                            trailingContent = if (state.user?.isConfirmed == true) {
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
                                onClick = vm::saveProfile,
                                isLoading = state.savingProfile,
                                size = KButtonSize.Sm
                            ) { Text(if (state.savingProfile) "Saving…" else "Save changes") }
                        }
                    }

                    // Security
                    SectionCard(
                        title = "Account Security",
                        description = "Update your password to secure access to your services."
                    ) {
                        FieldWithLabel(
                            "Current password",
                            state.currentPassword,
                            vm::onCurrentPasswordChange,
                            isPassword = true
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FieldWithLabel(
                                "New password",
                                state.newPassword,
                                vm::onNewPasswordChange,
                                modifier = Modifier.weight(1f),
                                isPassword = true
                            )
                            FieldWithLabel(
                                "Confirm",
                                state.confirmPassword,
                                vm::onConfirmPasswordChange,
                                modifier = Modifier.weight(1f),
                                isPassword = true
                            )
                        }
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            KButton(
                                onClick = vm::savePassword,
                                isLoading = state.savingPassword,
                                variant = KButtonVariant.Outline,
                                size = KButtonSize.Sm
                            ) { Text(if (state.savingPassword) "Updating…" else "Update password") }
                        }
                    }

                    // Subscriptions
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
                                fontSize = 12.sp, color = cs.onSurfaceVariant
                            )
                        } else {
                            state.subscriptions.forEach { sub ->
                                SubscriptionRow(
                                    sub = sub,
                                    onCancel = { vm.requestCancel(sub) }
                                )
                            }
                        }
                    }

                    // Logout
                    KButton(
                        onClick = {},
                        variant = KButtonVariant.Destructive,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ExitToApp, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Sign Out")
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
            onDismiss = vm::dismissCancel,
            onConfirm = vm::confirmCancel
        )
    }
}
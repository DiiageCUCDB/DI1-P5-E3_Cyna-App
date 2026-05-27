@file:OptIn(ExperimentalFoundationApi::class)

package com.cyna.app.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyna.app.ui.core.components.*
import com.cyna.app.ui.core.theme.AppTheme
import java.time.LocalDate
import dev.kindling.core.components.*

// ─────────────────────────────────────────────────────────────────────────────
//  Shared preview wrapper
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PreviewSurface(content: @Composable ColumnScope.() -> Unit) {
    AppTheme {
        Surface(
            color    = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text       = text,
        fontSize   = 11.sp,
        fontWeight = FontWeight.Medium,
        color      = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier   = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun Row2(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  1. Button
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Button — light", showBackground = true, widthDp = 360)
@Preview(name = "Button — dark",  showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewButtons() {
    PreviewSurface {
        SectionLabel("Variants")
        Row2 {
            KButton("Default",     onClick = {}, modifier = Modifier.weight(1f))
            KButton("Outline",     onClick = {}, variant = ButtonVariant.Outline,     modifier = Modifier.weight(1f))
        }
        Row2 {
            KButton("Secondary",   onClick = {}, variant = ButtonVariant.Secondary,   modifier = Modifier.weight(1f))
            KButton("Destructive", onClick = {}, variant = KButtonVariant.Destructive, modifier = Modifier.weight(1f))
        }
        Row2 {
            KButton("Ghost",       onClick = {}, variant = KButtonVariant.Ghost,       modifier = Modifier.weight(1f))
            KButton("Link",        onClick = {}, variant = KButtonVariant.Link,        modifier = Modifier.weight(1f))
        }

        SectionLabel("Sizes")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            KButton("Sm",      onClick = {}, size = KButtonSize.Sm)
            KButton("Default", onClick = {})
            KButton("Lg",      onClick = {}, size = KButtonSize.Lg)
        }

        SectionLabel("States")
        Row2 {
            KButton("Loading",  onClick = {}, isLoading = true, modifier = Modifier.weight(1f))
            KButton("Disabled", onClick = {}, enabled   = false, modifier = Modifier.weight(1f))
        }

        SectionLabel("Icon size")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KButton(onClick = {}, size = KButtonSize.Icon) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
            }
            KButton(onClick = {}, size = KButtonSize.Icon, variant = KButtonVariant.Outline) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
            }
            KButton(onClick = {}, size = KButtonSize.Icon, variant = KButtonVariant.Destructive) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  2. Label + Input + FormField
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Input — light", showBackground = true, widthDp = 360)
@Preview(name = "Input — dark",  showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewInput() {
    PreviewSurface {
        SectionLabel("Label")
        Label("Email address")
        Label("Disabled label", disabled = true)

        SectionLabel("Input — default")
        Input(value = "", onValueChange = {}, placeholder = "m@example.com")

        SectionLabel("Input — leading icon")
        Input(
            value = "", onValueChange = {}, placeholder = "m@example.com",
            leadingIcon = { Icon(Icons.Default.Email, null, modifier = Modifier.size(16.dp)) }
        )

        SectionLabel("Input — password / error / disabled")
        Input(value = "secret",    onValueChange = {}, isPassword = true)
        Input(value = "bad@",      onValueChange = {}, isError    = true)
        Input(value = "Read only", onValueChange = {}, enabled    = false)

        SectionLabel("FormField — normal")
        FormField(
            label = "Email", value = "", onValueChange = {},
            placeholder = "m@example.com", helperText = "We'll never share your email."
        )

        SectionLabel("FormField — error")
        FormField(
            label = "Email", value = "bad@", onValueChange = {},
            isError = true, errorMessage = "Please enter a valid email address."
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  3. InputOTP
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "InputOTP — light", showBackground = true, widthDp = 360)
@Preview(name = "InputOTP — dark",  showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewInputOTP() {
    PreviewSurface {
        SectionLabel("6-digit (partial)")
        InputOTP(value = "421", onValueChange = {}, length = 6)

        SectionLabel("Grouped 3-3")
        InputOTP(value = "12", onValueChange = {}, length = 6, groups = listOf(3, 3))

        SectionLabel("Complete")
        InputOTP(value = "123456", onValueChange = {}, length = 6)

        SectionLabel("Error")
        InputOTP(value = "123", onValueChange = {}, length = 6, isError = true)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  4. Combobox
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Combobox — light", showBackground = true, widthDp = 360)
@Preview(name = "Combobox — dark",  showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewCombobox() {
    val items = listOf(
        ComboboxItem("next",   "Next.js"),
        ComboboxItem("nuxt",   "Nuxt.js"),
        ComboboxItem("svelte", "SvelteKit"),
        ComboboxItem("astro",  "Astro"),
    )
    PreviewSurface {
        SectionLabel("Empty")
        Combobox(items = items, selected = null, onSelect = {}, placeholder = "Select framework…")

        SectionLabel("With selection")
        Combobox(items = items, selected = items[0], onSelect = {})

        SectionLabel("Disabled")
        Combobox(items = items, selected = null, onSelect = {}, enabled = false)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  5. DatePicker
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "DatePicker — light", showBackground = true, widthDp = 360)
@Preview(name = "DatePicker — dark",  showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDatePicker() {
    PreviewSurface {
        SectionLabel("Empty")
        DatePicker(selected = null, onSelect = {})

        SectionLabel("With selection")
        DatePicker(selected = LocalDate.of(2025, 6, 15), onSelect = {})

        SectionLabel("Disabled")
        DatePicker(selected = null, onSelect = {}, enabled = false)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  6. Skeleton
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Skeleton — light", showBackground = true, widthDp = 360)
@Preview(name = "Skeleton — dark",  showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewSkeleton() {
    PreviewSurface {
        SectionLabel("Text lines")
        Skeleton(modifier = Modifier.fillMaxWidth().height(14.dp))
        Skeleton(modifier = Modifier.fillMaxWidth(0.7f).height(14.dp))

        SectionLabel("List items")
        SkeletonListItem()
        SkeletonListItem()

        SectionLabel("Card")
        SkeletonCard(imageHeight = 110.dp)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  7. Spinner
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Spinner — light", showBackground = true, widthDp = 360)
@Preview(name = "Spinner — dark",  showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewSpinner() {
    PreviewSurface {
        SectionLabel("All sizes")
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Spinner(size = SpinnerSize.Sm)
            Spinner(size = SpinnerSize.Default)
            Spinner(size = SpinnerSize.Lg)
            Spinner(size = SpinnerSize.Xl)
        }

        SectionLabel("With label")
        Spinner(size = SpinnerSize.Default, label = "Loading data…")

        SectionLabel("Custom color")
        Spinner(size = SpinnerSize.Default, color = MaterialTheme.colorScheme.error, label = "Error color")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  8. Pagination
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Pagination — light", showBackground = true, widthDp = 360)
@Preview(name = "Pagination — dark",  showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewPagination() {
    PreviewSurface {
        SectionLabel("Page 1 of 5")
        Pagination(currentPage = 1, totalPages = 5, onPageChange = {})

        SectionLabel("Page 5 of 20 (ellipsis)")
        Pagination(currentPage = 5, totalPages = 20, onPageChange = {})

        SectionLabel("Last page")
        Pagination(currentPage = 20, totalPages = 20, onPageChange = {})
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  9. Stepper
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Stepper — light", showBackground = true, widthDp = 360)
@Preview(name = "Stepper — dark",  showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewStepper() {
    val steps = listOf(
        Step("Account", "Create account"),
        Step("Profile", "Tell us about you"),
        Step("Review",  "Confirm details"),
    )
    PreviewSurface {
        SectionLabel("Horizontal — step 0")
        Stepper(steps = steps, currentStep = 0)

        SectionLabel("Horizontal — step 1 (mid)")
        Stepper(steps = steps, currentStep = 1)

        SectionLabel("Vertical — step 1")
        Stepper(steps = steps, currentStep = 1, orientation = StepperOrientation.Vertical)

        SectionLabel("Error state")
        Stepper(
            steps = listOf(
                Step("Account", state = StepState.Completed),
                Step("Profile", state = StepState.Error),
                Step("Review",  state = StepState.Upcoming),
            ),
            currentStep = 1
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  10. Carousel
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Carousel — light", showBackground = true, widthDp = 360)
@Preview(name = "Carousel — dark",  showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewCarousel() {
    PreviewSurface {
        Carousel(pageCount = 3) { page ->
            val bgColors = listOf(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.tertiaryContainer,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColors[page]),
                contentAlignment = Alignment.Center
            ) {
                Text("Slide ${page + 1}", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  12. Empty
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Empty — light", showBackground = true, widthDp = 360)
@Preview(name = "Empty — dark",  showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewEmpty() {
    PreviewSurface {
        SectionLabel("Default — icon")
        Empty {
            EmptyHeader {
                EmptyMedia(variant = EmptyMediaVariant.Icon) {
                    Icon(Icons.Default.Info, null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                EmptyTitle("No Projects Yet")
                EmptyDescription("You haven't created any projects yet. Get started by creating your first project.")
            }
            EmptyContent {
                KButton(text = "Create Project", onClick = {})
                KButton(text = "Import Project",  onClick = {}, variant = KButtonVariant.Outline)
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        SectionLabel("Outline border")
        Empty(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .padding(24.dp)
        ) {
            EmptyHeader {
                EmptyMedia(variant = EmptyMediaVariant.Icon, iconBoxColor = MaterialTheme.colorScheme.primaryContainer) {
                    Icon(Icons.Default.Email, null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primary)
                }
                EmptyTitle("Cloud Storage Empty")
                EmptyDescription("Upload files to your cloud storage to access them anywhere.")
            }
            EmptyContent { KButton(text = "Upload Files", onClick = {}) }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        SectionLabel("Background fill")
        Empty(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(24.dp)
        ) {
            EmptyHeader {
                EmptyMedia(variant = EmptyMediaVariant.Icon) {
                    Icon(Icons.Default.Notifications, null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                EmptyTitle("No Notifications")
                EmptyDescription("You're all caught up. New notifications will appear here.")
            }
            EmptyContent { KButton(text = "Refresh", onClick = {}, variant = KButtonVariant.Outline) }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        SectionLabel("Avatar variant")
        Empty {
            EmptyHeader {
                EmptyMedia(variant = EmptyMediaVariant.Avatar) {
                    EmptyAvatar(
                        initials = "LR",
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor   = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                EmptyTitle("User Offline")
                EmptyDescription("This user is currently offline. Leave a message or try again later.")
            }
            EmptyContent { KButton(text = "Leave Message", onClick = {}) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  13. Direction
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Direction LTR — light", showBackground = true, widthDp = 360)
@Composable
private fun PreviewDirectionLtr() {
    AppTheme {
        DirectionProvider(direction = LayoutDirection.Ltr) {
            Surface(color = MaterialTheme.colorScheme.background) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionLabel("LTR — left to right")
                    DirectionDemoCard(isRtl = false)
                }
            }
        }
    }
}

@Preview(name = "Direction RTL — light", showBackground = true, widthDp = 360)
@Composable
private fun PreviewDirectionRtl() {
    AppTheme {
        DirectionProvider(direction = LayoutDirection.Rtl) {
            Surface(color = MaterialTheme.colorScheme.background) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionLabel("RTL — right to left")
                    DirectionDemoCard(isRtl = true)
                }
            }
        }
    }
}

@Composable
private fun DirectionDemoCard(isRtl: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        color    = MaterialTheme.colorScheme.surface,
        border   = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text       = if (isRtl) "تسجيل الدخول إلى حسابك" else "Sign in to your account",
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onBackground,
                textAlign  = if (isRtl) TextAlign.End else TextAlign.Start,
                modifier   = Modifier.fillMaxWidth()
            )
            FormField(
                label = if (isRtl) "البريد الإلكتروني" else "Email",
                value = "", onValueChange = {}, placeholder = "m@example.com"
            )
            FormField(
                label = if (isRtl) "كلمة المرور" else "Password",
                value = "", onValueChange = {}, isPassword = true
            )
            KButton(
                text = if (isRtl) "تسجيل الدخول" else "Sign in",
                onClick = {}, modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  14. Dialog
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Dialog — light", showBackground = true, widthDp = 360)
@Preview(name = "Dialog — dark",  showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDialog() {
    PreviewSurface {
        SectionLabel("AlertDialog (destructive) — static preview")
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Are you absolutely sure?", fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface)
                Text("This action cannot be undone. This will permanently delete your account.",
                    fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                    KButton("Cancel",         onClick = {}, variant = KButtonVariant.Outline,     size = KButtonSize.Sm)
                    KButton("Delete account", onClick = {}, variant = KButtonVariant.Destructive, size = KButtonSize.Sm)
                }
            }
        }

        SectionLabel("Free-form dialog — static preview")
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DialogHeader(title = "Edit profile", description = "Make changes here. Click save when done.")
                FormField(label = "Name", value = "John Doe", onValueChange = {})
                DialogFooter {
                    KButton("Cancel",       onClick = {}, variant = KButtonVariant.Outline, size = KButtonSize.Sm)
                    KButton("Save changes", onClick = {}, size = KButtonSize.Sm)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  15. Toaster (static cards)
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Toaster — light", showBackground = true, widthDp = 360)
@Preview(name = "Toaster — dark",  showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewToaster() {
    PreviewSurface {
        SectionLabel("Default toast")
        Surface(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
            color    = MaterialTheme.colorScheme.surface,
            border   = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface)
                Text("Event has been created.", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface)
            }
        }

        SectionLabel("Success toast")
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
            color = Color(0xFF166534)
        ) {
            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp),
                    tint = Color(0xFF4ADE80)
                )
                Column {
                    Text("Saved!", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                        color = Color(0xFFDCFCE7)
                    )
                    Text("Your changes have been saved.", fontSize = 11.sp,
                        color = Color(0xFFDCFCE7).copy(alpha = 0.8f))
                }
            }
        }

        SectionLabel("Error toast")
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.errorContainer) {
            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                Column {
                    Text("Uh oh!", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onErrorContainer)
                    Text("Something went wrong.", fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f))
                }
            }
        }

        SectionLabel("With action")
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("Email sent.", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                TextKButton(onClick = {}) {
                    Text("Undo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  16. Interactive showcase screen (for navigation)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * A live, interactive showcase of all components.
 * Add it to Navigation.kt and navigate here from HomeScreen for testing.
 *
 * In Navigation.kt:
 * ```
 * object ComponentShowcase : Destination(route = "showcase")
 * // then in NavHost:
 * composable(Destination.ComponentShowcase) { ComponentShowcaseScreen() }
 * ```
 */
@Composable
fun ComponentShowcaseScreen() {
    val scrollState = rememberScrollState()

    // Local state for interactive components
    var inputValue    by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var otpValue      by remember { mutableStateOf("") }
    var selectedItem  by remember { mutableStateOf<ComboboxItem?>(null) }
    var selectedDate  by remember { mutableStateOf<LocalDate?>(null) }
    var currentPage   by remember { mutableStateOf(1) }
    var currentStep   by remember { mutableStateOf(0) }
    var showDialog    by remember { mutableStateOf(false) }
    var showAlert     by remember { mutableStateOf(false) }
    var isLoading     by remember { mutableStateOf(false) }

    val frameworks = listOf(
        ComboboxItem("next",   "Next.js"),
        ComboboxItem("nuxt",   "Nuxt.js"),
        ComboboxItem("svelte", "SvelteKit"),
        ComboboxItem("astro",  "Astro"),
    )
    val steps = listOf(
        Step("Account"), Step("Profile"), Step("Review")
    )

    // Dialogs (rendered outside scroll)
    ShadAlertDialog(
        open          = showAlert,
        onDismiss     = { showAlert = false },
        title         = "Are you absolutely sure?",
        description   = "This action cannot be undone.",
        confirmLabel  = "Delete",
        isDestructive = true,
        onConfirm     = { showAlert = false; Toaster.error("Deleted!") }
    )
    ShadDialog(open = showDialog, onDismiss = { showDialog = false }) {
        DialogHeader(title = "Edit Profile", description = "Make changes here.")
        Spacer(Modifier.height(12.dp))
        FormField(label = "Name", value = inputValue, onValueChange = { inputValue = it }, placeholder = "John Doe")
        Spacer(Modifier.height(16.dp))
        DialogFooter {
            Button("Cancel",       onClick = { showDialog = false }, variant = ButtonVariant.Outline, size = ButtonSize.Sm)
            Button("Save changes", onClick = { showDialog = false; Toaster.success("Profile saved!") }, size = ButtonSize.Sm)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Header
            Text("Component Showcase", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground)
            Text("shadcn/ui components — Kotlin/Compose", fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            ShowcaseSection("Button") {
                Row2 {
                    Button("Default",     onClick = { Toaster.show("Default clicked") },              modifier = Modifier.weight(1f))
                    Button("Outline",     onClick = { Toaster.show("Outline clicked") }, variant = ButtonVariant.Outline,  modifier = Modifier.weight(1f))
                }
                Row2 {
                    Button("Ghost",       onClick = { Toaster.show("Ghost clicked") },   variant = ButtonVariant.Ghost,    modifier = Modifier.weight(1f))
                    Button("Destructive", onClick = { Toaster.error("Destructive!") },   variant = ButtonVariant.Destructive, modifier = Modifier.weight(1f))
                }
                Row2 {
                    Button("Loading",  onClick = {}, isLoading = true,  modifier = Modifier.weight(1f))
                    Button("Disabled", onClick = {}, enabled   = false, modifier = Modifier.weight(1f))
                }
            }

            ShowcaseSection("Input & FormField") {
                FormField(
                    label = "Email", value = inputValue, onValueChange = { inputValue = it },
                    placeholder = "m@example.com", helperText = "We'll never share your email."
                )
                FormField(
                    label = "Password", value = passwordValue, onValueChange = { passwordValue = it },
                    isPassword = true
                )
                FormField(
                    label = "Error example", value = "bad@", onValueChange = {},
                    isError = true, errorMessage = "Please enter a valid email."
                )
            }

            ShowcaseSection("InputOTP") {
                Text("6-digit code:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                InputOTP(value = otpValue, onValueChange = { if (it.length <= 6) otpValue = it }, length = 6)
                Text("Grouped (3-3):", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                InputOTP(value = otpValue, onValueChange = { if (it.length <= 6) otpValue = it }, length = 6, groups = listOf(3, 3))
            }

            ShowcaseSection("Combobox") {
                Combobox(items = frameworks, selected = selectedItem, onSelect = { selectedItem = it }, placeholder = "Select framework…")
            }

            ShowcaseSection("DatePicker") {
                DatePicker(selected = selectedDate, onSelect = { selectedDate = it }, placeholder = "Pick a date")
            }

            ShowcaseSection("Stepper") {
                Stepper(steps = steps, currentStep = currentStep)
                Spacer(Modifier.height(4.dp))
                Row2 {
                    Button("← Back",  onClick = { if (currentStep > 0) currentStep-- }, variant = ButtonVariant.Outline, size = ButtonSize.Sm, modifier = Modifier.weight(1f))
                    Button("Next →",  onClick = { if (currentStep < steps.lastIndex) currentStep++ }, size = ButtonSize.Sm, modifier = Modifier.weight(1f))
                }
            }

            ShowcaseSection("Carousel") {
                Carousel(pageCount = 3) { page ->
                    val bg = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.tertiaryContainer,
                    )[page]
                    Box(
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                            .clip(RoundedCornerShape(12.dp)).background(bg),
                        contentAlignment = Alignment.Center
                    ) { Text("Slide ${page + 1}", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) }
                }
            }

            ShowcaseSection("Pagination") {
                Pagination(currentPage = currentPage, totalPages = 15, onPageChange = { currentPage = it })
                Text("Page: $currentPage / 15", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            ShowcaseSection("Skeleton") {
                SkeletonListItem()
                SkeletonListItem()
                Spacer(Modifier.height(4.dp))
                SkeletonCard(imageHeight = 100.dp)
            }

            ShowcaseSection("Spinner") {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Spinner(size = SpinnerSize.Sm)
                    Spinner(size = SpinnerSize.Default)
                    Spinner(size = SpinnerSize.Lg)
                    Spinner(size = SpinnerSize.Xl)
                }
                Spinner(label = "Loading data…")
            }

            ShowcaseSection("Empty") {
                Empty {
                    EmptyHeader {
                        EmptyMedia(variant = EmptyMediaVariant.Icon) {
                            Icon(Icons.Default.Info, null, modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        EmptyTitle("No Projects Yet")
                        EmptyDescription("Get started by creating your first project.")
                    }
                    EmptyContent {
                        Button("Create Project", onClick = { Toaster.success("Creating project…") })
                    }
                }
            }

            ShowcaseSection("Dialog & AlertDialog") {
                Row2 {
                    Button("Open Dialog",  onClick = { showDialog = true }, modifier = Modifier.weight(1f))
                    Button("Open Alert",   onClick = { showAlert  = true }, variant = ButtonVariant.Destructive, modifier = Modifier.weight(1f))
                }
            }

            ShowcaseSection("Toaster") {
                Row2 {
                    Button("Default", onClick = { Toaster.show("Default toast") },                   size = ButtonSize.Sm, variant = ButtonVariant.Outline,     modifier = Modifier.weight(1f))
                    Button("Success", onClick = { Toaster.success("Saved!", "Changes applied.") },   size = ButtonSize.Sm,                                      modifier = Modifier.weight(1f))
                }
                Row2 {
                    Button("Error",   onClick = { Toaster.error("Failed!", "Try again.") },          size = ButtonSize.Sm, variant = ButtonVariant.Destructive, modifier = Modifier.weight(1f))
                    Button("Warning", onClick = { Toaster.warning("Watch out!") },                   size = ButtonSize.Sm, variant = ButtonVariant.Secondary,   modifier = Modifier.weight(1f))
                }
                Button(
                    text = "With Undo action",
                    onClick = {
                        Toaster.show(
                            message     = "Email deleted",
                            actionLabel = "Undo",
                            onAction    = { Toaster.info("Restored!") }
                        )
                    },
                    variant  = ButtonVariant.Outline,
                    size     = ButtonSize.Sm,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(48.dp))
        }

        // Toast overlay (always on top)
        ToasterHost()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Helper section wrapper
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ShowcaseSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground)
            Divider(modifier = Modifier.weight(1f).padding(start = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        }
        content()
    }
}
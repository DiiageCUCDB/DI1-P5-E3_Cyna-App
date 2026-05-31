package com.cyna.app.ui.core.components.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.kindling.core.components.KInput
import dev.kindling.core.components.KLabel
import dev.kindling.core.components.KMaskPattern
import dev.kindling.core.components.MaskInput

// ── Labeled field ─────────────────────────────────────────────────────────────

@Composable
fun FieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    isPassword: Boolean = false,
    isError: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        KLabel(label)
        if (trailingContent != null) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                KInput(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = placeholder,
                    enabled = enabled,
                    isPassword = isPassword,
                    isError = isError,
                    singleLine = singleLine,
                    maxLines = maxLines,
                    minLines = minLines,
                    modifier = Modifier.weight(1f)
                )
                trailingContent()
            }
        } else {
            KInput(
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder,
                enabled = enabled,
                isPassword = isPassword,
                isError = isError,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun FieldMaskWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    mask: KMaskPattern? = null,
    customPattern: String? = null,
    allowLetters: Boolean = mask?.keyboardType == KeyboardType.Text && mask.withoutMask.not(),
    withoutMask: Boolean = mask?.withoutMask ?: false,
    placeholder: String = mask?.placeholder
        ?: customPattern?.replace('#', '0')
        ?: "",
    enabled: Boolean = true,
    autoValidate: Boolean = true,
    onValidationChange: ((Boolean) -> Unit)? = null,
    isError: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        KLabel(label)
        if (trailingContent != null) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                MaskInput(
                    value = value,
                    onValueChange = onValueChange,
                    mask = mask,
                    customPattern = customPattern,
                    allowLetters = allowLetters,
                    withoutMask = withoutMask,
                    placeholder = placeholder,
                    enabled = enabled,
                    autoValidate = autoValidate,
                    onValidationChange = onValidationChange,
                    isError = isError,
                    modifier = Modifier.weight(1f)
                )
                trailingContent()
            }
        } else {
            MaskInput(
                value = value,
                onValueChange = onValueChange,
                mask = mask,
                customPattern = customPattern,
                allowLetters = allowLetters,
                withoutMask = withoutMask,
                placeholder = placeholder,
                enabled = enabled,
                autoValidate = autoValidate,
                onValidationChange = onValidationChange,
                isError = isError,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
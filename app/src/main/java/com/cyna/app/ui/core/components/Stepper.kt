package com.cyna.app.ui.core.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class StepState { Upcoming, Current, Completed, Error }

data class Step(
    val label: String,
    val description: String? = null,
    val state: StepState = StepState.Upcoming
)

enum class StepperOrientation { Horizontal, Vertical }

@Composable
fun Stepper(
    steps: List<Step>,
    currentStep: Int,
    modifier: Modifier = Modifier,
    orientation: StepperOrientation = StepperOrientation.Horizontal,
    onStepClick: ((index: Int) -> Unit)? = null
) {
    val resolvedSteps = steps.mapIndexed { index, step ->
        when {
            step.state != StepState.Upcoming -> step
            index < currentStep              -> step.copy(state = StepState.Completed)
            index == currentStep             -> step.copy(state = StepState.Current)
            else                             -> step.copy(state = StepState.Upcoming)
        }
    }

    if (orientation == StepperOrientation.Horizontal) {
        HorizontalStepper(resolvedSteps, modifier, onStepClick)
    } else {
        VerticalStepper(resolvedSteps, modifier, onStepClick)
    }
}

@Composable
private fun HorizontalStepper(steps: List<Step>, modifier: Modifier, onStepClick: ((Int) -> Unit)?) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        steps.forEachIndexed { index, step ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    if (index > 0) StepConnector(filled = step.state == StepState.Completed, modifier = Modifier.weight(1f))
                    StepBubble(step = step, number = index + 1, onClick = onStepClick?.let { { it(index) } })
                    if (index < steps.lastIndex) StepConnector(
                        filled = steps[index + 1].state == StepState.Completed || steps[index + 1].state == StepState.Current,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = step.label, fontSize = 12.sp,
                    fontWeight = if (step.state == StepState.Current) FontWeight.SemiBold else FontWeight.Normal,
                    color = stepLabelColor(step.state)
                )
                if (step.description != null) {
                    Text(text = step.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun VerticalStepper(steps: List<Step>, modifier: Modifier, onStepClick: ((Int) -> Unit)?) {
    Column(modifier = modifier) {
        steps.forEachIndexed { index, step ->
            Row(verticalAlignment = Alignment.Top) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    StepBubble(step = step, number = index + 1, onClick = onStepClick?.let { { it(index) } })
                    if (index < steps.lastIndex) {
                        Box(
                            modifier = Modifier.width(2.dp).height(32.dp).background(
                                if (steps[index + 1].state == StepState.Completed || steps[index + 1].state == StepState.Current)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Text(
                        text = step.label, fontSize = 14.sp,
                        fontWeight = if (step.state == StepState.Current) FontWeight.SemiBold else FontWeight.Normal,
                        color = stepLabelColor(step.state)
                    )
                    if (step.description != null) {
                        Text(text = step.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (index < steps.lastIndex) Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun StepBubble(step: Step, number: Int, onClick: (() -> Unit)? = null) {
    val cs = MaterialTheme.colorScheme

    val bgColor by animateColorAsState(
        targetValue = when (step.state) {
            StepState.Completed, StepState.Current -> cs.primary
            StepState.Error   -> cs.error
            StepState.Upcoming -> Color.Transparent
        },
        animationSpec = tween(300), label = "stepBg"
    )

    val borderColor by animateColorAsState(
        targetValue = when (step.state) {
            StepState.Completed, StepState.Current -> cs.primary
            StepState.Error   -> cs.error
            StepState.Upcoming -> cs.outline
        },
        animationSpec = tween(300), label = "stepBorder"
    )

    val contentColor = when (step.state) {
        StepState.Completed, StepState.Current -> cs.onPrimary
        StepState.Error   -> cs.onError
        StepState.Upcoming -> cs.onSurfaceVariant
    }

    Surface(
        shape  = CircleShape,
        color  = bgColor,
        border = if (step.state == StepState.Upcoming)
            BorderStroke(1.5.dp, borderColor)
        else null,
        modifier = Modifier
            .size(28.dp)
            .then(
                if (onClick != null) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onClick
                ) else Modifier
            )
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (step.state == StepState.Completed) {
                Icon(Icons.Default.Check, contentDescription = null, tint = contentColor, modifier = Modifier.size(14.dp))
            } else {
                Text(number.toString(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = contentColor)
            }
        }
    }
}

@Composable
private fun StepConnector(filled: Boolean, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val color by animateColorAsState(
        targetValue = if (filled) cs.primary else cs.outline,
        animationSpec = tween(300), label = "connectorColor"
    )
    Box(modifier = modifier.height(2.dp).background(color))
}

@Composable
private fun stepLabelColor(state: StepState): Color {
    val cs = MaterialTheme.colorScheme
    return when (state) {
        StepState.Current, StepState.Completed -> cs.onBackground
        StepState.Error   -> cs.error
        StepState.Upcoming -> cs.onSurfaceVariant
    }
}
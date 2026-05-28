package com.cyna.app.ui.screens.orders-history

import android.app.Application
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyna.app.domain.model.AccountOrder
import dev.kindling.core.components.*
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun formatPrice(amount: Double): String =
    NumberFormat.getCurrencyInstance(Locale.getDefault()).format(amount)

private fun formatDate(isoDate: String): String = runCatching {
    val inst = Instant.parse(isoDate)
    DateTimeFormatter.ofPattern("MMM d, yyyy")
        .withLocale(Locale.getDefault())
        .format(inst.atZone(ZoneId.systemDefault()))
}.getOrDefault(isoDate)

private fun getYear(isoDate: String): Int = runCatching {
    Instant.parse(isoDate).atZone(ZoneId.systemDefault()).year
}.getOrDefault(0)

// ── Status badge colors ───────────────────────────────────────────────────────

private data class StatusStyle(val bg: Color, val fg: Color)

@Composable
private fun statusStyle(status: String): StatusStyle {
    val cs = MaterialTheme.colorScheme
    return when (status) {
        "active"     -> StatusStyle(Color(0xFF166534).copy(.12f), Color(0xFF166534))
        "paid"       -> StatusStyle(Color(0xFF166534).copy(.12f), Color(0xFF166534))
        "terminated" -> StatusStyle(cs.surfaceVariant, cs.onSurfaceVariant)
        "refunded"   -> StatusStyle(cs.error.copy(.10f), cs.error)
        "pending"    -> StatusStyle(Color(0xFF78350F).copy(.12f), Color(0xFF92400E))
        "failed"     -> StatusStyle(cs.error.copy(.10f), cs.error)
        else         -> StatusStyle(cs.surfaceVariant, cs.onSurfaceVariant)
    }
}

// ── Status badge ──────────────────────────────────────────────────────────────

@Composable
private fun StatusBadge(status: String, label: String) {
    val style = statusStyle(status)
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = style.bg
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = style.fg
        )
    }
}

// ── Order row ─────────────────────────────────────────────────────────────────

@Composable
private fun OrderRow(order: AccountOrder, isLast: Boolean) {
    val cs = MaterialTheme.colorScheme

    val paymentLine = buildString {
        append(order.paymentMethod)
        if (order.paymentLast4.isNotBlank()) append(" ···· ${order.paymentLast4}")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (!isLast) Modifier.border(
                    width = 0.dp,
                    color = Color.Transparent,
                    shape = RoundedCornerShape(0.dp)
                ) else Modifier
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left: name + meta
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = order.productName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = cs.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    StatusBadge(order.status, order.statusLabel)
                }
                Text(
                    text = "${formatDate(order.createdAt)} · ${order.type}",
                    fontSize = 11.sp,
                    color = cs.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = paymentLine,
                    fontSize = 11.sp,
                    color = cs.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right: amount
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatPrice(order.total),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = cs.onSurface
                )
                if (order.invoiceUrl != null) {
                    Spacer(Modifier.height(4.dp))
                    KButton(
                        onClick = { /* open invoice URL */ },
                        variant = KButtonVariant.Outline,
                        size = KButtonSize.Xs
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text("PDF", fontSize = 10.sp)
                    }
                }
            }
        }

        if (!isLast) {
            HorizontalDivider(
                color = cs.outline.copy(.25f),
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

// ── Year group ────────────────────────────────────────────────────────────────

@Composable
private fun YearGroup(year: Int, orders: List<AccountOrder>, dimmed: Boolean) {
    val cs = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (dimmed) Modifier else Modifier)
    ) {
        // Year header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = year.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (dimmed) cs.onSurfaceVariant else cs.onSurface
            )
            Text(
                text = "${orders.size} order${if (orders.size != 1) "s" else ""}",
                fontSize = 11.sp,
                color = cs.onSurfaceVariant
            )
        }

        // Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = cs.surface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                cs.outline.copy(if (dimmed) .15f else .3f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (dimmed) Modifier else Modifier)
        ) {
            Column {
                orders.forEachIndexed { i, order ->
                    OrderRow(order = order, isLast = i == orders.size - 1)
                }
            }
        }
    }
}

// ── Skeleton row ──────────────────────────────────────────────────────────────

@Composable
private fun OrderRowSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Skeleton(modifier = Modifier.fillMaxWidth(.55f).height(12.dp))
            Skeleton(modifier = Modifier.fillMaxWidth(.75f).height(10.dp))
            Skeleton(modifier = Modifier.fillMaxWidth(.45f).height(10.dp))
        }
        Skeleton(modifier = Modifier.width(56.dp).height(18.dp))
    }
}

@Composable
private fun OrderHistorySkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        repeat(2) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Skeleton(modifier = Modifier.width(48.dp).height(14.dp))
                val cs = MaterialTheme.colorScheme
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = cs.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, cs.outline.copy(.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        repeat(2) { i ->
                            OrderRowSkeleton()
                            if (i == 0) {
                                HorizontalDivider(
                                    color = cs.outline.copy(.2f),
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Year filter chip ──────────────────────────────────────────────────────────

@Composable
private fun YearFilterRow(
    years: List<Int>,
    selectedYear: String,
    onYearChange: (String) -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // "All" chip
        val allActive = selectedYear == "all"
        Surface(
            onClick = { onYearChange("all") },
            shape = RoundedCornerShape(100.dp),
            color = if (allActive) cs.primary else cs.surfaceVariant,
            modifier = Modifier.height(28.dp)
        ) {
            Text(
                "All",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                fontSize = 11.sp,
                fontWeight = if (allActive) FontWeight.SemiBold else FontWeight.Normal,
                color = if (allActive) cs.onPrimary else cs.onSurfaceVariant
            )
        }

        years.forEach { year ->
            val isActive = selectedYear == year.toString()
            Surface(
                onClick = { onYearChange(year.toString()) },
                shape = RoundedCornerShape(100.dp),
                color = if (isActive) cs.primary else cs.surfaceVariant,
                modifier = Modifier.height(28.dp)
            ) {
                Text(
                    year.toString(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 11.sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isActive) cs.onPrimary else cs.onSurfaceVariant
                )
            }
        }
    }
}

// ── Main screen ───────────────────────────────────────────────────────────────

@Composable
fun OrderHistoryScreen(
    onNavigateBack: () -> Unit = {},
    vm: OrderHistoryViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val cs = MaterialTheme.colorScheme

    // Compute filtered + grouped data from ViewModel helper
    val filtered = remember(state.orders, state.searchQuery, state.selectedYear) {
        state.orders.filter { order ->
            val matchSearch = state.searchQuery.isBlank() ||
                    order.productName.contains(state.searchQuery, ignoreCase = true)
            val matchYear = state.selectedYear == "all" ||
                    getYear(order.createdAt).toString() == state.selectedYear
            matchSearch && matchYear
        }
    }

    val allYears = remember(state.orders) {
        state.orders.map { getYear(it.createdAt) }.distinct().sortedDescending()
    }

    val grouped = remember(filtered) {
        filtered
            .groupBy { getYear(it.createdAt) }
            .entries
            .sortedByDescending { it.key }
    }

    Scaffold(
        topBar = {
            Surface(color = cs.surface, tonalElevation = 1.dp) {
                Column {
                    // App bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        KButton(
                            onClick = onNavigateBack,
                            variant = KButtonVariant.Ghost,
                            size = KButtonSize.IconSm
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(4.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Billing & Payment",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = cs.onSurface
                            )
                            if (!state.loadingUser && state.user != null) {
                                Text(
                                    state.user!!.email,
                                    fontSize = 11.sp,
                                    color = cs.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Search field
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KInput(
                            value = state.searchQuery,
                            onValueChange = vm::onSearchChange,
                            placeholder = "Search by product name…",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = cs.onSurfaceVariant
                                )
                            },
                            trailingIcon = if (state.searchQuery.isNotBlank()) {
                                {
                                    KButton(
                                        onClick = { vm.onSearchChange("") },
                                        variant = KButtonVariant.Ghost,
                                        size = KButtonSize.IconXs
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            } else null,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Year filter chips
                    if (!state.loading && allYears.isNotEmpty()) {
                        YearFilterRow(
                            years = allYears,
                            selectedYear = state.selectedYear,
                            onYearChange = vm::onYearChange
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Loading ──────────────────────────────────────────────────────
            if (state.loading) {
                item { OrderHistorySkeleton() }
                return@LazyColumn
            }

            // ── Error ────────────────────────────────────────────────────────
            if (state.error != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = cs.error.copy(.07f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, cs.error.copy(.25f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = cs.error
                            )
                            Text(
                                "Failed to load orders",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = cs.onSurface
                            )
                            Text(
                                state.error!!,
                                fontSize = 12.sp,
                                color = cs.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            KButton("Retry", onClick = vm::retry, size = KButtonSize.Sm)
                        }
                    }
                }
                return@LazyColumn
            }

            // ── Empty state ──────────────────────────────────────────────────
            if (grouped.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                            tint = cs.onSurfaceVariant.copy(.4f)
                        )
                        Text(
                            if (state.searchQuery.isNotBlank() || state.selectedYear != "all")
                                "No matching orders"
                            else
                                "No orders yet",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = cs.onSurface
                        )
                        Text(
                            if (state.searchQuery.isNotBlank() || state.selectedYear != "all")
                                "Try a different search or year filter."
                            else
                                "Your billing history will appear here.",
                            fontSize = 12.sp,
                            color = cs.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        if (state.searchQuery.isNotBlank() || state.selectedYear != "all") {
                            Spacer(Modifier.height(4.dp))
                            KButton(
                                "Clear filters",
                                onClick = {
                                    vm.onSearchChange("")
                                    vm.onYearChange("all")
                                },
                                variant = KButtonVariant.Outline,
                                size = KButtonSize.Sm
                            )
                        }
                    }
                }
                return@LazyColumn
            }

            // ── Results summary ───────────────────────────────────────────────
            item {
                Text(
                    "${filtered.size} order${if (filtered.size != 1) "s" else ""}",
                    fontSize = 11.sp,
                    color = cs.onSurfaceVariant
                )
            }

            // ── Year groups ───────────────────────────────────────────────────
            items(grouped, key = { it.key }) { (year, yearOrders) ->
                val isDimmed = year != grouped.first().key
                YearGroup(
                    year = year,
                    orders = yearOrders,
                    dimmed = isDimmed
                )
            }

            // Bottom padding
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
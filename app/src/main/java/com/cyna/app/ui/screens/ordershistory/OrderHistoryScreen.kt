package com.cyna.app.ui.screens.ordershistory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyna.app.ui.core.components.ui.order.OrderRowSkeleton
import com.cyna.app.ui.core.components.ui.order.YearFilterRow
import com.cyna.app.ui.core.components.ui.order.YearGroup
import dev.kindling.compose.KScreen
import dev.kindling.core.components.*
import dev.kindling.utils.method.getYear

@Composable
private fun OrderHistorySkeleton() {
    val cs = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        repeat(2) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Skeleton(modifier = Modifier.width(48.dp).height(14.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = cs.surface,
                    border = BorderStroke(1.dp, cs.outline.copy(.2f)),
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

@Composable
fun OrderHistoryScreen(navController: NavController) {
    KScreen(
        viewModel = viewModel<OrderHistoryViewModel>(),
        navController = navController
    ) { state, viewModel ->
        OrderHistoryContent(
            state          = state,
            onSearchChange = viewModel::onSearchChange,
            onYearChange   = viewModel::onYearChange,
            onRetry        = viewModel::retry,
        )
    }
}

@Composable
private fun OrderHistoryContent(
    state: OrderHistoryContracts.UiState = OrderHistoryContracts.UiState(),
    onSearchChange: (String) -> Unit = {},
    onYearChange: (String) -> Unit = {},
    onRetry: () -> Unit = {},
) {
    val cs = MaterialTheme.colorScheme

    // Filtre : recherche sur items[].productNameSnapshot (via primaryProductName / itemsSummary)
    val filtered = remember(state.orders, state.searchQuery, state.selectedYear) {
        state.orders.filter { order ->
            val matchSearch = state.searchQuery.isBlank() ||
                    order.primaryProductName.contains(state.searchQuery, ignoreCase = true) ||
                    order.itemsSummary.contains(state.searchQuery, ignoreCase = true)
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(Modifier.width(4.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Billing & Payment",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = cs.onSurface
                            )
                            // Affiche user.email depuis UserProfileDto
                            if (!state.loadingUser && state.user != null) {
                                Text(
                                    state.user.email,
                                    fontSize = 11.sp,
                                    color = cs.onSurfaceVariant
                                )
                            }
                        }
                    }

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
                            onValueChange = onSearchChange,
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
                                        onClick = { onSearchChange("") },
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

                    if (!state.loading && allYears.isNotEmpty()) {
                        YearFilterRow(
                            years = allYears,
                            selectedYear = state.selectedYear,
                            onYearChange = onYearChange
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
            if (state.loading) {
                item { OrderHistorySkeleton() }
                return@LazyColumn
            }

            if (state.error != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = cs.error.copy(.07f),
                        border = BorderStroke(1.dp, cs.error.copy(.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Lock,
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
                                state.error,
                                fontSize = 12.sp,
                                color = cs.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            KButton("Retry", onClick = onRetry, size = KButtonSize.Sm)
                        }
                    }
                }
                return@LazyColumn
            }

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
                            Icons.Default.Menu,
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
                                    onSearchChange("")
                                    onYearChange("all")
                                },
                                variant = KButtonVariant.Outline,
                                size = KButtonSize.Sm
                            )
                        }
                    }
                }
                return@LazyColumn
            }

            item {
                Text(
                    "${filtered.size} order${if (filtered.size != 1) "s" else ""}",
                    fontSize = 11.sp,
                    color = cs.onSurfaceVariant
                )
            }

            items(grouped, key = { it.key }) { (year, yearOrders) ->
                YearGroup(
                    year = year,
                    orders = yearOrders,
                    dimmed = year != grouped.first().key
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
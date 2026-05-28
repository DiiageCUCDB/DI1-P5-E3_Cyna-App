package com.cyna.app.ui.screens.catalog

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cyna.app.domain.model.Category
import com.cyna.app.domain.model.Product
import dev.kindling.core.components.*
import java.text.NumberFormat
import java.util.Locale

// ── Price formatter ───────────────────────────────────────────────────────────

private fun formatMonthlyPrice(price: Double): String =
    NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 0
    }.format(price)

// ── Sort options ─────────────────────────────────────────────────────────────

private val SORT_OPTIONS = listOf(
    "relevance" to "Relevance",
    "price_asc" to "Price: Low to High",
    "price_desc" to "Price: High to Low",
    "name" to "Name A–Z"
)

// ── Product card ─────────────────────────────────────────────────────────────

@Composable
private fun ProductCard(product: Product, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = cs.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (product.isAvailable) cs.outline.copy(.3f) else cs.outline.copy(.15f)
        ),
        tonalElevation = 0.dp
    ) {
        Column {
            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(cs.surfaceVariant)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .then(if (!product.isAvailable) Modifier.background(Color.Black.copy(.25f)) else Modifier)
                )
                // Availability badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (product.isAvailable) Color(0xFF166534).copy(.9f)
                        else cs.surfaceVariant.copy(.9f)
                    ) {
                        Text(
                            text = if (product.isAvailable) "Available" else "Unavailable",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (product.isAvailable) Color.White else cs.onSurfaceVariant
                        )
                    }
                }
            }

            // Content
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    product.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = cs.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    product.description,
                    fontSize = 11.sp,
                    color = cs.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    if (product.isAvailable) {
                        Column {
                            Text(
                                formatMonthlyPrice(product.priceMonthly),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = cs.primary
                            )
                            Text("/month", fontSize = 9.sp, color = cs.onSurfaceVariant)
                        }
                    } else {
                        Spacer(Modifier.size(1.dp))
                    }
                    KButton(
                        "Details",
                        onClick = {},
                        size = KButtonSize.Sm,
                        variant = if (product.isAvailable) KButtonVariant.Default else KButtonVariant.Outline,
                        enabled = product.isAvailable
                    )
                }
            }
        }
    }
}

// ── Product card skeleton ─────────────────────────────────────────────────────

@Composable
private fun ProductCardSkeleton() {
    val cs = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = cs.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, cs.outline.copy(.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Skeleton(modifier = Modifier.fillMaxWidth().height(110.dp))
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Skeleton(modifier = Modifier.fillMaxWidth(.7f).height(12.dp))
                Skeleton(modifier = Modifier.fillMaxWidth().height(10.dp))
                Skeleton(modifier = Modifier.fillMaxWidth(.85f).height(10.dp))
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Skeleton(modifier = Modifier.width(60.dp).height(20.dp))
                    Skeleton(modifier = Modifier.width(64.dp).height(28.dp))
                }
            }
        }
    }
}

// ── Filter sheet ─────────────────────────────────────────────────────────────

@Composable
private fun FilterSheet(
    categories: List<Category>,
    selectedCategories: List<String>,
    maxPrice: Double?,
    onlyAvailable: Boolean,
    onCategoryToggle: (String) -> Unit,
    onMaxPriceChange: (Double?) -> Unit,
    onAvailableToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    var sliderValue by remember { mutableStateOf(maxPrice?.toFloat() ?: 1000f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filter Services", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
            KButton(onClick = onDismiss, variant = KButtonVariant.Ghost, size = KButtonSize.IconSm) {
                Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
            }
        }

        HorizontalDivider(color = cs.outline.copy(.3f))

        // Categories
        if (categories.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Categories", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
                categories.forEach { cat ->
                    val isSelected = cat.id in selectedCategories
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onCategoryToggle(cat.id) }
                            .background(if (isSelected) cs.primary.copy(.08f) else Color.Transparent)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(cat.name, fontSize = 13.sp, color = if (isSelected) cs.primary else cs.onSurface)
                        if (isSelected) {
                            Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp), tint = cs.primary)
                        }
                    }
                }
            }
        }

        // Budget slider
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Monthly Budget", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
                Text(
                    if (sliderValue >= 1000f) "No limit" else "$${sliderValue.toInt()}",
                    fontSize = 12.sp, color = cs.primary, fontWeight = FontWeight.Medium
                )
            }
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = {
                    onMaxPriceChange(if (sliderValue >= 1000f) null else sliderValue.toDouble())
                },
                valueRange = 0f..1000f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = cs.primary,
                    activeTrackColor = cs.primary
                )
            )
        }

        // Available only toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Available only", fontSize = 13.sp, color = cs.onSurface)
            Switch(
                checked = onlyAvailable,
                onCheckedChange = onAvailableToggle,
                colors = SwitchDefaults.colors(checkedThumbColor = cs.onPrimary, checkedTrackColor = cs.primary)
            )
        }

        KButton("Apply Filters", onClick = onDismiss, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(8.dp))
    }
}

// ── Sort bottom sheet ─────────────────────────────────────────────────────────

@Composable
private fun SortSheet(current: String, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("Sort by", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
        Spacer(Modifier.height(8.dp))
        SORT_OPTIONS.forEach { (value, label) ->
            val isActive = value == current
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSelect(value); onDismiss() }
                    .background(if (isActive) cs.primary.copy(.08f) else Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    label, fontSize = 14.sp,
                    color = if (isActive) cs.primary else cs.onSurface,
                    fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
                )
                if (isActive) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = cs.primary)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

// ── Active filter chip ────────────────────────────────────────────────────────

@Composable
private fun FilterChip(label: String, onRemove: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(cs.primary.copy(.12f))
            .border(1.dp, cs.primary.copy(.3f), RoundedCornerShape(100.dp))
            .padding(start = 10.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, fontSize = 11.sp, color = cs.primary, fontWeight = FontWeight.Medium)
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(cs.primary.copy(.2f))
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Close, null, modifier = Modifier.size(10.dp), tint = cs.primary)
        }
    }
}

// ── Pagination bar ────────────────────────────────────────────────────────────

@Composable
private fun PaginationRow(currentPage: Int, totalPages: Int, onPageChange: (Int) -> Unit) {
    if (totalPages <= 1) return
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Pagination(
            currentPage = currentPage,
            totalPages = totalPages,
            onPageChange = onPageChange
        )
    }
    Text(
        "Page $currentPage of $totalPages",
        modifier = Modifier.fillMaxWidth(),
        fontSize = 11.sp,
        color = cs.onSurfaceVariant,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

// ── Main screen ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    vm: CatalogViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val cs = MaterialTheme.colorScheme

    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }

    // Count active filters
    val activeFilterCount = buildList {
        if (state.searchQuery.isNotBlank()) add("search")
        addAll(state.selectedCategories)
        if (state.maxPrice != null) add("price")
        if (state.onlyAvailable) add("available")
    }.size

    // Bottom sheets
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = cs.surface
        ) {
            FilterSheet(
                categories = state.categories,
                selectedCategories = state.selectedCategories,
                maxPrice = state.maxPrice,
                onlyAvailable = state.onlyAvailable,
                onCategoryToggle = vm::onCategoryToggle,
                onMaxPriceChange = vm::onMaxPriceChange,
                onAvailableToggle = vm::onAvailableToggle,
                onDismiss = { showFilterSheet = false }
            )
        }
    }

    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false },
            containerColor = cs.surface
        ) {
            SortSheet(
                current = state.sortBy,
                onSelect = vm::onSortChange,
                onDismiss = { showSortSheet = false }
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Search bar + controls ────────────────────────────────────────────
        Surface(color = cs.surface, tonalElevation = 1.dp) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Service Catalog",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = cs.onSurface
                )
                // Search field
                KInput(
                    value = state.searchQuery,
                    onValueChange = vm::onSearchChange,
                    placeholder = "Title or description…",
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, modifier = Modifier.size(16.dp), tint = cs.onSurfaceVariant)
                    },
                    trailingIcon = if (state.searchQuery.isNotBlank()) {
                        {
                            KButton(
                                onClick = { vm.onSearchChange("") },
                                variant = KButtonVariant.Ghost,
                                size = KButtonSize.IconXs
                            ) { Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp)) }
                        }
                    } else null
                )
                // Filter/sort controls row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Filter button
                    KButton(
                        onClick = { showFilterSheet = true },
                        variant = if (activeFilterCount > 0) KButtonVariant.Default else KButtonVariant.Outline,
                        size = KButtonSize.Sm,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.FilterList, null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Filter${if (activeFilterCount > 0) " ($activeFilterCount)" else ""}")
                    }
                    // Sort button
                    KButton(
                        onClick = { showSortSheet = true },
                        variant = KButtonVariant.Outline,
                        size = KButtonSize.Sm,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Sort, null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(SORT_OPTIONS.firstOrNull { it.first == state.sortBy }?.second ?: "Sort")
                    }
                }
            }
        }

        // ── Active filter chips ──────────────────────────────────────────────
        if (state.selectedCategories.isNotEmpty() || state.maxPrice != null || state.onlyAvailable) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                state.selectedCategories.forEach { id ->
                    val name = state.categories.firstOrNull { it.id == id }?.name ?: id
                    FilterChip(label = name, onRemove = { vm.onCategoryToggle(id) })
                }
                if (state.maxPrice != null) {
                    FilterChip(
                        label = "≤ $${state.maxPrice!!.toInt()}/mo",
                        onRemove = { vm.onMaxPriceChange(null) }
                    )
                }
                if (state.onlyAvailable) {
                    FilterChip(label = "Available", onRemove = { vm.onAvailableToggle(false) })
                }
                Spacer(Modifier.weight(1f))
                KButton(
                    "Reset",
                    onClick = vm::resetFilters,
                    variant = KButtonVariant.Ghost,
                    size = KButtonSize.Sm
                )
            }
        }

        // ── Results header ───────────────────────────────────────────────────
        if (!state.loadingProducts) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${state.total} service${if (state.total != 1) "s" else ""}",
                    fontSize = 12.sp,
                    color = cs.onSurfaceVariant
                )
            }
        }

        // ── Grid ─────────────────────────────────────────────────────────────
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (state.loadingProducts) {
                items(CatalogViewModel.PAGE_SIZE) {
                    ProductCardSkeleton()
                }
            } else if (state.products.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            null,
                            modifier = Modifier.size(40.dp),
                            tint = cs.onSurfaceVariant.copy(.5f)
                        )
                        Text(
                            "No results found",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = cs.onSurface
                        )
                        Text(
                            "Try adjusting your search criteria.",
                            fontSize = 12.sp,
                            color = cs.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        KButton("Reset Filters", onClick = vm::resetFilters, variant = KButtonVariant.Outline, size = KButtonSize.Sm)
                    }
                }
            } else {
                items(state.products, key = { it.id }) { product ->
                    ProductCard(product = product)
                }

                // Pagination at the bottom of the grid
                if (state.totalPages > 1) {
                    item(span = { GridItemSpan(2) }) {
                        PaginationRow(
                            currentPage = state.currentPage,
                            totalPages = state.totalPages,
                            onPageChange = vm::onPageChange
                        )
                    }
                }
            }
        }
    }
}
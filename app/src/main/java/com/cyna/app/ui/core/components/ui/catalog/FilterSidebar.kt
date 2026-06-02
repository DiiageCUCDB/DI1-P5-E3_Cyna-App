package com.cyna.app.ui.core.components.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyna.app.domain.model.Category
import com.cyna.app.ui.screens.catalog.CatalogContracts
import dev.kindling.core.components.KInput
import dev.kindling.core.components.KLabel
import dev.kindling.core.components.Skeleton

// ---------------------------------------------------------------------------
// FilterSidebar — miroir de filter-sidebar.jsx
// ---------------------------------------------------------------------------

/**
 * Panneau de filtres latéral.
 * Utilise exclusivement des composants Kindling ([KInput], [KLabel], [Skeleton])
 * pour rester cohérent avec le design shadcn/ui de la version React.
 */
@Composable
fun FilterSidebar(
    categories: List<Category>,
    filters: CatalogContracts.Filters,
    onSearchChange: (String) -> Unit,
    onCategoryToggle: (String) -> Unit,
    onMaxPriceChange: (Double?) -> Unit,
    onOnlyAvailableChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .width(224.dp)           // w-56 → 14 × 16 = 224 dp
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // ── Titre ────────────────────────────────────────────────────────────
        KLabel(
            text     = "Filtres",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // ── Recherche textuelle ───────────────────────────────────────────────
        FilterSection(title = "Recherche") {
            KInput(
                value         = filters.search,
                onValueChange = onSearchChange,
                placeholder   = "Rechercher un service…",
                modifier      = Modifier.fillMaxWidth()
            )
        }

        // ── Catégories ────────────────────────────────────────────────────────
        FilterSection(title = "Catégories") {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                categories.forEach { category ->
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier              = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked         = category.id in filters.categories,
                            onCheckedChange = { onCategoryToggle(category.id) },
                            colors          = CheckboxDefaults.colors(
                                checkedColor = cs.primary
                            )
                        )
                        KLabel(
                            text     = category.name,
                            disabled = false,
                            color    = cs.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ── Budget ────────────────────────────────────────────────────────────
        FilterSection(title = "Budget") {
            Slider(
                value       = (filters.maxPrice ?: 1000.0).toFloat(),
                onValueChange = { onMaxPriceChange(it.toDouble()) },
                valueRange  = 0f..1000f,
                steps       = 99,   // 100 pas → steps = count - 1
                colors      = SliderDefaults.colors(
                    thumbColor       = cs.primary,
                    activeTrackColor = cs.primary
                )
            )
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                KLabel(text = "0 €",  color = cs.onSurfaceVariant)
                KLabel(
                    text  = if (filters.maxPrice == null || filters.maxPrice == 1000.0)
                        "Max"
                    else
                        "${filters.maxPrice.toInt()} €",
                    color = cs.onSurfaceVariant
                )
            }
        }

        // ── Disponibilité ─────────────────────────────────────────────────────
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier              = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Switch(
                checked         = filters.onlyAvailable,
                onCheckedChange = onOnlyAvailableChange,
                colors          = SwitchDefaults.colors(
                    checkedThumbColor  = cs.primary,
                    checkedTrackColor  = cs.primary.copy(alpha = 0.3f)
                )
            )
            KLabel(text = "Disponibles uniquement")
        }
    }
}

// ---------------------------------------------------------------------------
// FilterSection — bloc avec titre + séparateur + contenu (même structure JSX)
// ---------------------------------------------------------------------------

@Composable
private fun FilterSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        KLabel(
            text  = title,
            color = MaterialTheme.colorScheme.onSurface
        )
        content()
        HorizontalDivider(
            modifier  = Modifier.padding(top = 8.dp),
            color     = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp
        )
    }
}

// ---------------------------------------------------------------------------
// FilterSidebarSkeleton — squelette de chargement
// ---------------------------------------------------------------------------

/** Squelette de chargement du panneau de filtres. */
@Composable
fun FilterSidebarSkeleton() {
    Column(
        modifier = Modifier
            .width(224.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Skeleton(modifier = Modifier.width(80.dp).height(16.dp))
        repeat(4) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Skeleton(modifier = Modifier.width(60.dp).height(12.dp))
                Skeleton(modifier = Modifier.fillMaxWidth().height(36.dp))
            }
        }
    }
}
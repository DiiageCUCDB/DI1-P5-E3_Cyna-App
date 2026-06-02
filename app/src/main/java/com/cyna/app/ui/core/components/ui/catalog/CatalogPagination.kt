package com.cyna.app.ui.core.components.ui.catalog

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.kindling.core.components.Pagination
import dev.kindling.core.components.PaginationContent
import dev.kindling.core.components.PaginationEllipsis
import dev.kindling.core.components.PaginationItem
import dev.kindling.core.components.PaginationLink
import dev.kindling.core.components.PaginationNext
import dev.kindling.core.components.PaginationPrevious

// ---------------------------------------------------------------------------
// CatalogPagination — mirrors catalog-pagination.jsx
// ---------------------------------------------------------------------------

/**
 * Pagination du catalogue.
 * Utilise les composants Kindling [Pagination] / [PaginationLink] / etc.,
 * qui eux-mêmes reprennent le style shadcn/ui — exactement comme la version React.
 *
 * @param currentPage  Page courante (1-indexed).
 * @param totalPages   Nombre total de pages.
 * @param onPageChange Callback appelé avec la nouvelle page.
 * @param modifier     Modificateur appliqué au conteneur racine.
 */
@Composable
fun CatalogPagination(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (totalPages <= 1) return

    val pages = buildPageRange(currentPage, totalPages)

    Pagination(modifier = modifier.padding(top = 32.dp)) {
        PaginationContent {
            // Bouton Précédent
            PaginationItem {
                PaginationPrevious(
                    onClick  = { onPageChange((currentPage - 1).coerceAtLeast(1)) },
                    enabled  = currentPage > 1,
                    text     = "Précédent"
                )
            }

            // Numéros de pages avec ellipses
            pages.forEachIndexed { index, page ->
                if (page == null) {
                    PaginationItem { PaginationEllipsis() }
                } else {
                    PaginationItem {
                        PaginationLink(
                            page     = page,
                            isActive = page == currentPage,
                            onClick  = { onPageChange(page) }
                        )
                    }
                }
            }

            // Bouton Suivant
            PaginationItem {
                PaginationNext(
                    onClick  = { onPageChange((currentPage + 1).coerceAtMost(totalPages)) },
                    enabled  = currentPage < totalPages,
                    text     = "Suivant"
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// buildPageRange — port de la fonction JS buildPageRange (lib/utils)
// ---------------------------------------------------------------------------

/**
 * Construit la liste de pages à afficher avec des `null` pour les ellipses.
 * Miroir exact de la fonction `buildPageRange` utilisée dans la version React.
 */
internal fun buildPageRange(current: Int, total: Int): List<Int?> {
    val delta = 2
    val range = mutableListOf<Int>()

    for (i in 1..total) {
        if (i == 1 || i == total || (i >= current - delta && i <= current + delta)) {
            range.add(i)
        }
    }

    val result = mutableListOf<Int?>()
    var last: Int? = null

    for (page in range) {
        if (last != null) {
            when {
                page - last == 2 -> result.add(last + 1) // gap d'un seul numéro → affiche-le
                page - last != 1 -> result.add(null)      // gap plus large → ellipse
            }
        }
        result.add(page)
        last = page
    }

    return result
}
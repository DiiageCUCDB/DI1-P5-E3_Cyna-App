package com.cyna.app.ui.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TableColumn<T>(
    val key: String,
    val header: String,
    val weight: Float = 1f,
    val sortable: Boolean = false,
    val cell: @Composable RowScope.(row: T) -> Unit
)

enum class SortDirection { Asc, Desc, None }

@Composable
fun <T> DataTable(
    columns: List<TableColumn<T>>,
    data: List<T>,
    modifier: Modifier = Modifier,
    pageSize: Int = Int.MAX_VALUE,
    striped: Boolean = false,
    onSort: ((key: String, direction: SortDirection) -> Unit)? = null,
    emptyContent: @Composable () -> Unit = {
        Box(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            contentAlignment = Alignment.Center
        ) { Text("No results.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) }
    }
) {
    val cs = MaterialTheme.colorScheme
    var sortKey by remember { mutableStateOf<String?>(null) }
    var sortDir by remember { mutableStateOf(SortDirection.None) }
    var page    by remember { mutableStateOf(1) }

    val totalPages  = if (pageSize == Int.MAX_VALUE) 1 else maxOf(1, (data.size + pageSize - 1) / pageSize)
    val displayData = if (pageSize == Int.MAX_VALUE) data else data.drop((page - 1) * pageSize).take(pageSize)

    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, cs.outline, RoundedCornerShape(8.dp))
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cs.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                columns.forEach { col ->
                    Row(
                        modifier = Modifier
                            .weight(col.weight)
                            .then(
                                if (col.sortable && onSort != null)
                                    Modifier.clickable {
                                        val newDir = when {
                                            sortKey != col.key            -> SortDirection.Asc
                                            sortDir == SortDirection.Asc  -> SortDirection.Desc
                                            else                          -> SortDirection.None
                                        }
                                        sortKey = if (newDir == SortDirection.None) null else col.key
                                        sortDir = newDir
                                        onSort(col.key, newDir)
                                    }
                                else Modifier
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = col.header, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                            color = cs.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        if (col.sortable && onSort != null) {
                            Icon(
                                imageVector = when {
                                    sortKey == col.key && sortDir == SortDirection.Asc  -> Icons.Default.KeyboardArrowUp
                                    sortKey == col.key && sortDir == SortDirection.Desc -> Icons.Default.KeyboardArrowDown
                                    else -> Icons.Default.KeyboardArrowDown
                                },
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (sortKey == col.key) cs.primary else cs.onSurface.copy(alpha = 0.38f)
                            )
                        }
                    }
                }
            }

            // Use Divider (works on all Material3 versions)
            Divider(color = cs.outline, thickness = 1.dp)

            if (displayData.isEmpty()) {
                emptyContent()
            } else {
                LazyColumn {
                    itemsIndexed(displayData) { index, row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (striped && index % 2 == 1) cs.surfaceVariant.copy(alpha = 0.3f)
                                    else Color.Transparent
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            columns.forEach { col ->
                                Row(modifier = Modifier.weight(col.weight)) { col.cell(this, row) }
                            }
                        }
                        if (index < displayData.lastIndex) {
                            Divider(color = cs.outline.copy(alpha = 0.5f), thickness = 1.dp)
                        }
                    }
                }
            }
        }

        if (totalPages > 1) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Page $page of $totalPages", fontSize = 13.sp, color = cs.onSurfaceVariant)
                Pagination(currentPage = page, totalPages = totalPages, onPageChange = { page = it }, siblingCount = 1)
            }
        }
    }
}
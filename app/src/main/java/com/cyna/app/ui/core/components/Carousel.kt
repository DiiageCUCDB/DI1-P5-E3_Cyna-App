package com.cyna.app.ui.core.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class CarouselOrientation { Horizontal, Vertical }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Carousel(
    pageCount: Int,
    modifier: Modifier = Modifier,
    orientation: CarouselOrientation = CarouselOrientation.Horizontal,
    showArrows: Boolean = true,
    showDots: Boolean = true,
    autoPlayMs: Long? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 0.dp),
    pageSpacing: Dp = 8.dp,
    state: PagerState = rememberPagerState { pageCount },
    content: @Composable BoxScope.(page: Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    if (autoPlayMs != null) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(autoPlayMs)
                val next = (state.currentPage + 1) % pageCount
                state.animateScrollToPage(next)
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box {
            if (orientation == CarouselOrientation.Horizontal) {
                HorizontalPager(
                    state = state,
                    contentPadding = contentPadding,
                    pageSpacing = pageSpacing,
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    Box(modifier = Modifier.fillMaxWidth()) { content(page) }
                }
            } else {
                VerticalPager(
                    state = state,
                    contentPadding = contentPadding,
                    pageSpacing = pageSpacing,
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    Box(modifier = Modifier.fillMaxWidth()) { content(page) }
                }
            }

            if (showArrows && orientation == CarouselOrientation.Horizontal) {
                CarouselArrow(
                    direction = CarouselArrowDir.Left,
                    enabled = state.currentPage > 0,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp),
                    onClick = { scope.launch { state.animateScrollToPage(state.currentPage - 1) } }
                )
                CarouselArrow(
                    direction = CarouselArrowDir.Right,
                    enabled = state.currentPage < pageCount - 1,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                    onClick = { scope.launch { state.animateScrollToPage(state.currentPage + 1) } }
                )
            }
        }

        if (showDots && pageCount > 1) {
            CarouselDots(
                pageCount = pageCount,
                currentPage = state.currentPage,
                onClick = { page -> scope.launch { state.animateScrollToPage(page) } }
            )
        }
    }
}

private enum class CarouselArrowDir { Left, Right }

@Composable
private fun CarouselArrow(
    direction: CarouselArrowDir,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(6.dp),
        color = cs.surface.copy(alpha = 0.85f),
        contentColor = if (enabled) cs.onSurface else cs.onSurface.copy(alpha = 0.38f),
        shadowElevation = 2.dp,
        modifier = modifier.size(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (direction == CarouselArrowDir.Left)
                    Icons.Default.KeyboardArrowLeft else Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun CarouselDots(pageCount: Int, currentPage: Int, onClick: (Int) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            Surface(
                onClick = { onClick(index) },
                shape = CircleShape,
                color = if (isActive) cs.primary else cs.outline,
                modifier = Modifier.size(if (isActive) 8.dp else 6.dp)
            ) {}
        }
    }
}
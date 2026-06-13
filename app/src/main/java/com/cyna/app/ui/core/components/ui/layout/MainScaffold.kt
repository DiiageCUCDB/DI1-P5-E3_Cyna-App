package com.cyna.app.ui.core.components.ui.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun MainScaffold(
    showLayout: Boolean = true,
    navController: NavController? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            if (showLayout) {
                Header(
                    onAccountClick = { /* Handled via SessionManager in Header */ },
                    onSearchClick = { /* Mock Search */ },
                    onMenuClick = { /* Mobile Drawer Mock */ }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.weight(1f)) {
                content(innerPadding)
            }
            if (showLayout) {
                Footer()
            }
        }
    }
}

package com.cyna.app.ui.core.components.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cyna.app.R
import com.cyna.app.data.local.SessionManager
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    onSearchClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onLanguageChange: (String) -> Unit = {}
) {
    val sessionManager: SessionManager = koinInject()
    val user by sessionManager.user.collectAsState()
    var showLanguageMenu by remember { mutableStateOf(false) }
    var showUserMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = "CYNA",
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = stringResource(R.string.header_search_hint))
            }
            
            Box {
                IconButton(onClick = { showLanguageMenu = true }) {
                    Icon(Icons.Default.Language, contentDescription = "Language")
                }
                DropdownMenu(
                    expanded = showLanguageMenu,
                    onDismissRequest = { showLanguageMenu = false }
                ) {
                    DropdownMenuItem(text = { Text("English") }, onClick = { onLanguageChange("en"); showLanguageMenu = false })
                    DropdownMenuItem(text = { Text("Français") }, onClick = { onLanguageChange("fr"); showLanguageMenu = false })
                }
            }

            Box {
                IconButton(onClick = { 
                    if (user != null) showUserMenu = true else onAccountClick() 
                }) {
                    Icon(
                        imageVector = if (user != null) Icons.Default.AccountCircle else Icons.Default.Login,
                        contentDescription = "Account",
                        tint = if (user != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                if (user != null) {
                    DropdownMenu(
                        expanded = showUserMenu,
                        onDismissRequest = { showUserMenu = false }
                    ) {
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            text = { Text(stringResource(R.string.header_menu_account)) },
                            onClick = { showUserMenu = false; onAccountClick() }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Default.Logout, null) },
                            text = { Text(stringResource(R.string.header_menu_logout)) },
                            onClick = { showUserMenu = false; sessionManager.clearSession() }
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

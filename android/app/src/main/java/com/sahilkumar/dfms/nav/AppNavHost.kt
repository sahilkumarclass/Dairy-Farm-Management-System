package com.sahilkumar.dfms.nav

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sahilkumar.dfms.R
import com.sahilkumar.dfms.core.data.Session
import com.sahilkumar.dfms.core.ui.LocaleManager
import com.sahilkumar.dfms.feature.bills.BillsScreen
import com.sahilkumar.dfms.feature.customers.CustomersScreen
import com.sahilkumar.dfms.feature.dashboard.DashboardScreen
import com.sahilkumar.dfms.feature.expenses.ExpensesScreen
import com.sahilkumar.dfms.feature.herd.CowDetailScreen
import com.sahilkumar.dfms.feature.herd.HerdScreen
import com.sahilkumar.dfms.feature.milk.MilkEntriesScreen
import com.sahilkumar.dfms.feature.staff.StaffScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainShell(session: Session, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val destinations = remember(session.role) { TopDestination.forRole(session.role) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentTop = destinations.firstOrNull { it.route == currentRoute }
    val isTopLevel = currentTop != null

    var showLanguageDialog by remember { mutableStateOf(false) }

    val title = when {
        currentTop != null -> stringResource(currentTop.labelRes)
        currentRoute == Routes.COW_DETAIL -> stringResource(R.string.nav_herd)
        else -> stringResource(R.string.app_name)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
                    Text(session.fullName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(session.role.name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                destinations.forEach { dest ->
                    NavigationDrawerItem(
                        icon = { Icon(dest.icon, contentDescription = null) },
                        label = { Text(stringResource(dest.labelRes)) },
                        selected = dest.route == currentRoute,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (dest.route != currentRoute) {
                                navController.navigate(dest.route) {
                                    popUpTo(TopDestination.DASHBOARD.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Language, contentDescription = null) },
                    label = { Text(stringResource(R.string.common_language)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showLanguageDialog = true
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                    label = { Text(stringResource(R.string.action_sign_out)) },
                    selected = false,
                    onClick = onLogout,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        if (isTopLevel) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = null)
                            }
                        } else {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            }
                        }
                    },
                )
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = TopDestination.DASHBOARD.route,
                modifier = Modifier.padding(padding),
            ) {
                composable(TopDestination.DASHBOARD.route) { DashboardScreen() }
                composable(TopDestination.CUSTOMERS.route) { CustomersScreen() }
                composable(TopDestination.MILK.route) { MilkEntriesScreen() }
                composable(TopDestination.EXPENSES.route) { ExpensesScreen() }
                composable(TopDestination.BILLS.route) { BillsScreen() }
                composable(TopDestination.HERD.route) {
                    HerdScreen(onOpenCow = { id -> navController.navigate(Routes.cowDetail(id)) })
                }
                composable(Routes.COW_DETAIL) { entry ->
                    CowDetailScreen(cowId = entry.arguments?.getString("cowId").orEmpty())
                }
            }
        }
    }

    if (showLanguageDialog) {
        LanguageDialog(onDismiss = { showLanguageDialog = false })
    }
}

@Composable
private fun LanguageDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_language)) },
        text = {
            Column {
                val labels = mapOf(
                    "en" to stringResource(R.string.language_english),
                    "hi" to stringResource(R.string.language_hindi),
                    "pa" to stringResource(R.string.language_punjabi),
                )
                LocaleManager.supported.forEach { tag ->
                    NavigationDrawerItem(
                        label = { Text(labels[tag] ?: tag) },
                        selected = LocaleManager.current() == tag,
                        onClick = {
                            LocaleManager.set(tag)
                            onDismiss()
                        },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close)) }
        },
    )
}

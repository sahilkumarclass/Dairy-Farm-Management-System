package com.sahilkumar.dfms.nav

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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

// Number of destinations shown directly in the bottom bar before the rest
// collapse into a "More" sheet. Keeps the bar within Material's 3-5 item guidance.
private const val MAX_INLINE_TABS = 4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainShell(session: Session, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val destinations = remember(session.role) { TopDestination.forRole(session.role) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentTop = destinations.firstOrNull { it.route == currentRoute }
    val isTopLevel = currentTop != null

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }

    val title = when {
        currentTop != null -> stringResource(currentTop.labelRes)
        currentRoute == Routes.COW_DETAIL -> stringResource(R.string.nav_herd)
        else -> stringResource(R.string.app_name)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (!isTopLevel) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showOverflowMenu = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = showOverflowMenu,
                        onDismissRequest = { showOverflowMenu = false },
                    ) {
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Filled.Language, contentDescription = null) },
                            text = { Text(stringResource(R.string.common_language)) },
                            onClick = {
                                showOverflowMenu = false
                                showLanguageDialog = true
                            },
                        )
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                            text = { Text(stringResource(R.string.action_sign_out)) },
                            onClick = {
                                showOverflowMenu = false
                                onLogout()
                            },
                        )
                    }
                },
            )
        },
        bottomBar = {
            AppBottomBar(
                destinations = destinations,
                currentRoute = currentRoute,
                navController = navController,
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
            composable(TopDestination.STAFF.route) { StaffScreen() }
            composable(Routes.COW_DETAIL) { entry ->
                CowDetailScreen(cowId = entry.arguments?.getString("cowId").orEmpty())
            }
        }
    }

    if (showLanguageDialog) {
        LanguageDialog(onDismiss = { showLanguageDialog = false })
    }
}

/** Switch top-level tabs while preserving each tab's saved back stack. */
private fun NavController.switchTab(route: String, currentRoute: String?) {
    if (route == currentRoute) return
    navigate(route) {
        popUpTo(TopDestination.DASHBOARD.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBottomBar(
    destinations: List<TopDestination>,
    currentRoute: String?,
    navController: NavController,
) {
    val inline = if (destinations.size <= MAX_INLINE_TABS + 1) {
        destinations
    } else {
        destinations.take(MAX_INLINE_TABS)
    }
    val overflow = destinations.drop(inline.size)

    var showMoreSheet by remember { mutableStateOf(false) }
    val overflowActive = overflow.any { it.route == currentRoute }

    NavigationBar {
        inline.forEach { dest ->
            val selected = dest.route == currentRoute ||
                (dest == TopDestination.HERD && currentRoute == Routes.COW_DETAIL)
            NavigationBarItem(
                selected = selected,
                onClick = { navController.switchTab(dest.route, currentRoute) },
                icon = { Icon(dest.icon, contentDescription = null) },
                label = {
                    Text(
                        text = stringResource(dest.labelRes),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
        if (overflow.isNotEmpty()) {
            NavigationBarItem(
                selected = overflowActive || showMoreSheet,
                onClick = { showMoreSheet = true },
                icon = { Icon(Icons.Filled.MoreHoriz, contentDescription = null) },
                label = {
                    Text(
                        text = stringResource(R.string.nav_more),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    }

    if (showMoreSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showMoreSheet = false },
            sheetState = sheetState,
        ) {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                overflow.forEach { dest ->
                    NavigationDrawerItem(
                        icon = { Icon(dest.icon, contentDescription = null) },
                        label = { Text(stringResource(dest.labelRes)) },
                        selected = dest.route == currentRoute,
                        onClick = {
                            showMoreSheet = false
                            navController.switchTab(dest.route, currentRoute)
                        },
                    )
                }
            }
        }
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

package com.sahilkumar.dfms.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.sahilkumar.dfms.R
import com.sahilkumar.dfms.core.ui.screenContentPadding

/** Common scaffold for paged list screens: FAB, snackbar, load states and rows. */
@Composable
fun <T : Any> PagedListScaffold(
    items: LazyPagingItems<T>,
    emptyMessage: String,
    snackbar: SnackbarHostState,
    onAdd: (() -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    row: @Composable (T) -> Unit,
) {
    Scaffold(
        snackbarHost = { AppSnackbarHost(snackbar) },
        floatingActionButton = {
            if (onAdd != null) {
                FloatingActionButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
                }
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            header?.invoke()
            when (val refresh = items.loadState.refresh) {
                is LoadState.Loading -> LoadingState()
                is LoadState.Error -> ErrorState(
                    message = refresh.error.message ?: stringResource(R.string.generic_error),
                    onRetry = { items.retry() },
                )
                else -> if (items.itemCount == 0) {
                    EmptyState(emptyMessage)
                } else {
                    LazyColumn(
                        contentPadding = screenContentPadding(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(items.itemCount) { index ->
                            items[index]?.let { row(it) }
                        }
                    }
                }
            }
        }
    }
}

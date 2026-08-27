package com.arslan.ndna.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.arslan.ndna.model.AppItem
import com.arslan.ndna.model.SearchState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    state: SearchState,
    onBack: () -> Unit,
    onOpen: (AppItem) -> Unit,
    onLoadMore: () -> Unit,
    onErrorShown: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbar = remember { SnackbarHostState() }
    ErrorEffect(state.error, snackbar, onErrorShown)
    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("${state.items.size} results") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        ResultsBody(state, onOpen, onLoadMore, Modifier.padding(padding))
    }
}

@Composable
private fun ErrorEffect(error: String?, snackbar: SnackbarHostState, onShown: () -> Unit) {
    LaunchedEffect(error) {
        if (error == null) return@LaunchedEffect
        snackbar.showSnackbar(error)
        onShown()
    }
}

@Composable
private fun ResultsBody(
    state: SearchState,
    onOpen: (AppItem) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier
) {
    if (state.loading && state.items.isEmpty()) return Loading(modifier)
    if (state.items.isEmpty()) return Empty(modifier)
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.items, key = { it.id }) { item -> AppCard(item) { onOpen(item) } }
        item { LoadMore(state, onLoadMore) }
    }
}

@Composable
private fun LoadMore(state: SearchState, onLoadMore: () -> Unit) {
    if (!state.canLoadMore) return
    OutlinedButton(
        onClick = onLoadMore,
        enabled = !state.loading,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(if (state.loading) "Loading…" else "Load more")
    }
}

@Composable
private fun Loading(modifier: Modifier) {
    Box(modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
}

@Composable
private fun Empty(modifier: Modifier) {
    Box(modifier.fillMaxSize(), Alignment.Center) {
        Text(
            "No matches. Loosen the filters.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

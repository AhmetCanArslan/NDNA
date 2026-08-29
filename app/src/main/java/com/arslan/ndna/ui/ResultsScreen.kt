package com.arslan.ndna.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arslan.ndna.model.AppItem
import com.arslan.ndna.model.Preview
import com.arslan.ndna.model.SearchState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ResultsScreen(
    state: SearchState,
    onBack: () -> Unit,
    preview: Preview?,
    onOpen: (AppItem) -> Unit,
    onPreview: (AppItem) -> Unit,
    onClosePreview: () -> Unit,
    onBlock: (AppItem) -> Unit,
    onUnblock: (String) -> Unit,
    onLoadMore: () -> Unit,
    onErrorShown: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    ErrorEffect(state.error, snackbar, onErrorShown)
    val block: (AppItem) -> Unit = { item ->
        onBlock(item)
        scope.launch {
            val action = snackbar.showSnackbar("Blocked ${item.name}", actionLabel = "Undo")
            if (action == SnackbarResult.ActionPerformed) onUnblock(item.id)
        }
    }
    preview?.let {
        ReadmeDialog(
            preview = it,
            onDismiss = onClosePreview,
            onBlock = {
                onClosePreview()
                block(it.item)
            },
            onOpen = {
                onClosePreview()
                onOpen(it.item)
            }
        )
    }
    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "${state.items.size} results",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            if (state.loading) "Searching…" else "Tap a card to open it",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = { BackButton(onBack) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        ResultsBody(state, onPreview, block, onLoadMore, Modifier.padding(padding))
    }
}

@Composable
private fun BackButton(onBack: () -> Unit) {
    FilledIconButton(
        onClick = onBack,
        shape = MaterialTheme.shapes.medium,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back") }
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
    onBlock: (AppItem) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier
) {
    if (state.loading && state.items.isEmpty()) return Loading(modifier)
    if (state.items.isEmpty()) return Empty(modifier)
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(state.items, key = { it.id }) { item ->
            AppCard(item, Modifier.animateItem(), onBlock = { onBlock(item) }) { onOpen(item) }
        }
        item { LoadMore(state, onLoadMore) }
    }
}

@Composable
private fun LoadMore(state: SearchState, onLoadMore: () -> Unit) {
    AnimatedVisibility(
        visible = state.canLoadMore,
        enter = fadeIn() + expandVertically()
    ) {
        Button(
            onClick = onLoadMore,
            enabled = !state.loading,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
        ) {
            Text(if (state.loading) "Loading…" else "Load more")
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Loading(modifier: Modifier) {
    Box(modifier.fillMaxSize(), Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ContainedLoadingIndicator()
            Text(
                "Digging through repos…",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun Empty(modifier: Modifier) {
    Box(modifier.fillMaxSize().padding(32.dp), Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.SearchOff,
                    null,
                    Modifier.size(44.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                "Nothing matched",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                "Loosen the filters and try again.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

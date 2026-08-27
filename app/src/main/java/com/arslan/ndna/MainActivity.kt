package com.arslan.ndna

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arslan.ndna.model.AppItem
import com.arslan.ndna.ui.FiltersScreen
import com.arslan.ndna.ui.ResultsScreen
import com.arslan.ndna.ui.SearchViewModel
import com.arslan.ndna.ui.SettingsScreen
import com.arslan.ndna.ui.theme.NDNATheme

private enum class Screen { FILTERS, RESULTS, SETTINGS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { NDNATheme { NdnaApp() } }
    }
}

@Composable
private fun NdnaApp(vm: SearchViewModel = viewModel()) {
    var screen by remember { mutableStateOf(Screen.FILTERS) }
    val filters by vm.filters.collectAsState()
    val state by vm.state.collectAsState()
    val syncMessage by vm.syncMessage.collectAsState()
    val context = LocalContext.current
    val open: (AppItem) -> Unit = { item ->
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.url)))
    }
    LaunchedEffect(state.loading) { if (state.loading) screen = Screen.RESULTS }
    when (screen) {
        Screen.FILTERS -> FiltersScreen(
            filters = filters,
            onChange = vm::update,
            onSearch = { vm.search() },
            onSettings = { screen = Screen.SETTINGS }
        )
        Screen.RESULTS -> ResultsScreen(
            state = state,
            onBack = { screen = Screen.FILTERS },
            onOpen = open,
            onLoadMore = { vm.loadMore() },
            onErrorShown = vm::clearError
        )
        Screen.SETTINGS -> SettingsScreen(
            initialToken = vm.tokenStore.get(),
            syncMessage = syncMessage,
            onSaveToken = vm.tokenStore::set,
            onSync = { vm.syncFdroid() },
            onBack = { screen = Screen.FILTERS }
        )
    }
}

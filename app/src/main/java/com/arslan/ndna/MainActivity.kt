package com.arslan.ndna

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.PredictiveBackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arslan.ndna.model.AppItem
import com.arslan.ndna.ui.FiltersScreen
import com.arslan.ndna.ui.ResultsScreen
import com.arslan.ndna.ui.SearchViewModel
import com.arslan.ndna.ui.SettingsScreen
import com.arslan.ndna.ui.theme.NDNATheme
import kotlin.coroutines.cancellation.CancellationException

private enum class Screen(val depth: Int) { FILTERS(0), RESULTS(1), SETTINGS(1) }

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
    val context = LocalContext.current
    val open: (AppItem) -> Unit = { item ->
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.url)))
    }
    LaunchedEffect(state.loading) { if (state.loading) screen = Screen.RESULTS }

    // A seekable state lets the back gesture scrub the same transition the taps
    // play, so there is one animation instead of a gesture effect plus a jump.
    val transitionState = remember { SeekableTransitionState(Screen.FILTERS) }
    LaunchedEffect(screen) { transitionState.animateTo(screen) }

    PredictiveBackHandler(enabled = screen != Screen.FILTERS) { events ->
        try {
            events.collect { transitionState.seekTo(it.progress, Screen.FILTERS) }
            screen = Screen.FILTERS
        } catch (cancelled: CancellationException) {
            transitionState.animateTo(transitionState.currentState)
            throw cancelled
        }
    }

    val transition = rememberTransition(transitionState, "screen")
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        transition.AnimatedContent(
            transitionSpec = { sharedAxisX(forward = targetState.depth > initialState.depth) }
        ) { current ->
            when (current) {
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
                    onSaveToken = vm.tokenStore::set,
                    onBack = { screen = Screen.FILTERS }
                )
            }
        }
    }
}

// Material shared-axis X: screens slide along one axis and cross-fade, so the
// direction of travel reads as forward or back.
private fun sharedAxisX(forward: Boolean): ContentTransform {
    val shift = 120
    val dir = if (forward) 1 else -1
    return ContentTransform(
        targetContentEnter = slideInHorizontally(tween(320)) { dir * shift } +
            fadeIn(tween(220, delayMillis = 60)),
        initialContentExit = slideOutHorizontally(tween(320)) { -dir * shift } +
            fadeOut(tween(160)),
        sizeTransform = SizeTransform(clip = false)
    )
}

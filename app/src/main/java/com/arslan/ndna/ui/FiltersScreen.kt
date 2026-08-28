package com.arslan.ndna.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arslan.ndna.model.Filters
import com.arslan.ndna.model.Lang
import com.arslan.ndna.model.Recency
import com.arslan.ndna.model.Triple3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersScreen(
    filters: Filters,
    onChange: ((Filters) -> Filters) -> Unit,
    onSearch: () -> Unit,
    onSettings: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { FiltersBar(scrollBehavior, onSettings) },
        floatingActionButton = { SearchFab(onSearch) }
    ) { padding ->
        FiltersBody(filters, onChange, Modifier.padding(padding))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun FiltersBar(scrollBehavior: TopAppBarScrollBehavior, onSettings: () -> Unit) {
    LargeFlexibleTopAppBar(
        title = { Text("New Day\nNew App", style = MaterialTheme.typography.displaySmall) },
        subtitle = { Text("Find fresh open-source Android apps") },
        scrollBehavior = scrollBehavior,
        actions = {
            FilledIconButton(
                onClick = onSettings,
                shape = MaterialTheme.shapes.medium,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) { Icon(Icons.Rounded.Settings, "Settings") }
        }
    )
}

@Composable
private fun SearchFab(onSearch: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onSearch,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = MaterialTheme.shapes.large,
        icon = { Icon(Icons.Rounded.Search, null) },
        text = { Text("Search", style = MaterialTheme.typography.titleMedium) }
    )
}

@Composable
private fun FiltersBody(
    filters: Filters,
    onChange: ((Filters) -> Filters) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SourceToggles(filters, onChange)
        SectionCard("Language") {
            ChipGroup(Lang.entries, { it == filters.lang }, { it.label }) { lang ->
                onChange { it.copy(lang = lang) }
            }
        }
        SectionCard("Stars", MaterialTheme.colorScheme.tertiary) { StarsRow(filters, onChange) }
        SectionCard("Last commit", MaterialTheme.colorScheme.secondary) {
            ChipGroup(Recency.entries, { it == filters.recency }, { it.label }) { r ->
                onChange { it.copy(recency = r) }
            }
        }
        SectionCard("Shizuku support") {
            ChipGroup(Triple3.entries, { it == filters.shizuku }, { it.label }) { s ->
                onChange { it.copy(shizuku = s) }
            }
        }
        SectionCard("Keywords", MaterialTheme.colorScheme.tertiary) {
            KeywordsField(filters, onChange)
        }
        Spacer96()
    }
}

@Composable
private fun Spacer96() {
    Column(Modifier.height(96.dp)) {}
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SourceToggles(filters: Filters, onChange: ((Filters) -> Filters) -> Unit) {
    SectionCard("Sources", MaterialTheme.colorScheme.secondary) {
        ButtonGroup(
            overflowIndicator = {},
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            customItem(
                buttonGroupContent = {
                    SourceToggle("GitHub", filters.github, Modifier.weight(1f)) {
                        onChange { it.copy(github = !it.github) }
                    }
                },
                menuContent = {}
            )
            customItem(
                buttonGroupContent = {
                    SourceToggle("F-Droid", filters.fdroid, Modifier.weight(1f)) {
                        onChange { it.copy(fdroid = !it.fdroid) }
                    }
                },
                menuContent = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SourceToggle(
    label: String,
    checked: Boolean,
    modifier: Modifier,
    onToggle: () -> Unit
) {
    val pop by animateFloatAsState(if (checked) 1f else 0.96f, label = "sourcePop")
    ToggleButton(
        checked = checked,
        onCheckedChange = { onToggle() },
        modifier = modifier.graphicsLayer { scaleX = pop; scaleY = pop }
    ) {
        Icon(Icons.Rounded.Bolt, null, Modifier.size(18.dp))
        Text(label, Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun StarsRow(filters: Filters, onChange: ((Filters) -> Filters) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NumberField("Min", filters.minStars, Modifier.weight(1f)) { v ->
            onChange { it.copy(minStars = v) }
        }
        NumberField("Max", filters.maxStars, Modifier.weight(1f)) { v ->
            onChange { it.copy(maxStars = v) }
        }
    }
}

@Composable
private fun NumberField(label: String, value: Int, modifier: Modifier, onValue: (Int) -> Unit) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { text -> onValue(text.filter { it.isDigit() }.take(7).toIntOrNull() ?: 0) },
        label = { Text(label) },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}

@Composable
private fun KeywordsField(filters: Filters, onChange: ((Filters) -> Filters) -> Unit) {
    OutlinedTextField(
        value = filters.keywords,
        onValueChange = { text -> onChange { it.copy(keywords = text) } },
        placeholder = { Text("launcher, root, adb…") },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    )
}

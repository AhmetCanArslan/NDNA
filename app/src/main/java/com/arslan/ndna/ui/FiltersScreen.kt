package com.arslan.ndna.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onSearch,
                icon = { Icon(Icons.Default.Search, null) },
                text = { Text("Search") }
            )
        }
    ) { padding ->
        FiltersBody(filters, onChange, Modifier.padding(padding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersBar(
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior,
    onSettings: () -> Unit
) {
    LargeTopAppBar(
        title = { Text("New Day New App") },
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, "Settings") }
        }
    )
}

@Composable
private fun FiltersBody(
    filters: Filters,
    onChange: ((Filters) -> Filters) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SourceChips(filters, onChange)
        ChipGroup("Language", Lang.entries, { it == filters.lang }, { it.label }) { lang ->
            onChange { it.copy(lang = lang) }
        }
        StarsRow(filters, onChange)
        ChipGroup("Last commit", Recency.entries, { it == filters.recency }, { it.label }) { r ->
            onChange { it.copy(recency = r) }
        }
        ChipGroup("Shizuku support", Triple3.entries, { it == filters.shizuku }, { it.label }) { s ->
            onChange { it.copy(shizuku = s) }
        }
        KeywordsField(filters, onChange)
    }
}

@Composable
private fun SourceChips(filters: Filters, onChange: ((Filters) -> Filters) -> Unit) {
    val sources = listOf("GitHub" to filters.github, "F-Droid" to filters.fdroid)
    ChipGroup("Sources", sources, { it.second }, { it.first }) { source ->
        if (source.first == "GitHub") onChange { it.copy(github = !it.github) }
        else onChange { it.copy(fdroid = !it.fdroid) }
    }
}

@Composable
private fun StarsRow(filters: Filters, onChange: ((Filters) -> Filters) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NumberField("Min stars", filters.minStars, Modifier.weight(1f)) { v ->
            onChange { it.copy(minStars = v) }
        }
        NumberField("Max stars", filters.maxStars, Modifier.weight(1f)) { v ->
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
        shape = androidx.compose.material3.MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}

@Composable
private fun KeywordsField(filters: Filters, onChange: ((Filters) -> Filters) -> Unit) {
    OutlinedTextField(
        value = filters.keywords,
        onValueChange = { text -> onChange { it.copy(keywords = text) } },
        label = { Text("Keywords") },
        placeholder = { Text("launcher, root, adb…") },
        singleLine = true,
        shape = androidx.compose.material3.MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    )
}

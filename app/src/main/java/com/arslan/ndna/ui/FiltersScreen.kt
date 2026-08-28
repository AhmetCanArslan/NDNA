package com.arslan.ndna.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arslan.ndna.model.Filters
import com.arslan.ndna.model.Lang
import com.arslan.ndna.model.Recency
import com.arslan.ndna.model.Triple3

@Composable
fun FiltersScreen(
    filters: Filters,
    onChange: ((Filters) -> Filters) -> Unit,
    onSearch: () -> Unit,
    onSettings: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = { SearchFab(onSearch) }
    ) { padding ->
        FiltersBody(filters, onChange, onSettings, Modifier.padding(padding))
    }
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
    onSettings: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Hero(onSettings)
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

// Header lives at the very top of the scrolling content, not in a collapsing
// app bar where the title would sit at the bottom edge.
@Composable
private fun Hero(onSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "New Day\nNew App",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Find fresh open-source Android apps",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        FilledIconButton(
            onClick = onSettings,
            shape = MaterialTheme.shapes.medium,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) { Icon(Icons.Rounded.Settings, "Settings") }
    }
}

@Composable
private fun Spacer96() {
    Column(Modifier.height(96.dp)) {}
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

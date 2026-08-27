package com.arslan.ndna.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    initialToken: String,
    syncMessage: String?,
    onSaveToken: (String) -> Unit,
    onSync: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        SettingsBody(initialToken, syncMessage, onSaveToken, onSync, Modifier.padding(padding))
    }
}

@Composable
private fun SettingsBody(
    initialToken: String,
    syncMessage: String?,
    onSaveToken: (String) -> Unit,
    onSync: () -> Unit,
    modifier: Modifier
) {
    var token by remember { mutableStateOf(initialToken) }
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "A GitHub token raises the search limit from 10 to 30 requests per minute.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TokenField(token) { token = it }
        Button(onClick = { onSaveToken(token) }) { Text("Save token") }
        Button(onClick = onSync, modifier = Modifier.fillMaxWidth()) { Text("Sync F-Droid catalog") }
        if (syncMessage != null) Text(syncMessage, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun TokenField(token: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = token,
        onValueChange = onChange,
        label = { Text("GitHub token (optional)") },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    )
}

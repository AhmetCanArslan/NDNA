package com.arslan.ndna.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri

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
        TokenHelpCard()
        TokenField(token) { token = it }
        Button(onClick = { onSaveToken(token) }) { Text("Save token") }
        Button(onClick = onSync, modifier = Modifier.fillMaxWidth()) { Text("Sync F-Droid catalog") }
        if (syncMessage != null) Text(syncMessage, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun TokenHelpCard() {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("How to get a GitHub token", style = MaterialTheme.typography.titleMedium)
            TOKEN_STEPS.forEach { StepText(it) }
            TextButton(onClick = { openTokenPage(context) }) { Text("Open GitHub token page") }
        }
    }
}

@Composable
private fun StepText(step: String) {
    Text(
        step,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

private fun openTokenPage(context: android.content.Context) {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TOKEN_URL)))
}

private const val TOKEN_URL = "https://github.com/settings/tokens/new?description=NDNA&scopes="

private val TOKEN_STEPS = listOf(
    "Without a token searches are limited to 10 per minute, with one you get 30.",
    "1. Open the token page below and sign in to GitHub.",
    "2. Give the token a name, for example NDNA.",
    "3. Set an expiration date.",
    "4. Select no scopes at all: public search needs none.",
    "5. Tap Generate token and copy it.",
    "6. Paste it in the field below and tap Save token."
)

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

package com.arslan.ndna.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    initialToken: String,
    onSaveToken: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Settings", style = MaterialTheme.typography.headlineSmall)
                },
                navigationIcon = {
                    FilledIconButton(
                        onClick = onBack,
                        shape = MaterialTheme.shapes.medium,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        SettingsBody(initialToken, onSaveToken, Modifier.padding(padding))
    }
}

@Composable
private fun SettingsBody(
    initialToken: String,
    onSaveToken: (String) -> Unit,
    modifier: Modifier
) {
    var token by remember { mutableStateOf(initialToken) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        RateLimitCard()
        TokenHelpCard()
        SectionCard("GitHub token", MaterialTheme.colorScheme.tertiary) {
            TokenField(token) { token = it }
            Button(
                onClick = { onSaveToken(token) },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Rounded.Key, null, Modifier.size(18.dp))
                Text("Save token", Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun RateLimitCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.onPrimary) {
                Icon(
                    Icons.Rounded.Key,
                    null,
                    Modifier.padding(10.dp).size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column {
                Text(
                    "10 → 30 searches / min",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    "A token triples your search budget.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun TokenHelpCard() {
    val context = LocalContext.current
    SectionCard("How to get a token") {
        TOKEN_STEPS.forEach { StepText(it) }
        TextButton(
            onClick = { openTokenPage(context) },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.AutoMirrored.Rounded.OpenInNew, null, Modifier.size(18.dp))
            Text("Open GitHub token page", Modifier.padding(start = 8.dp))
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
        label = { Text("Token (optional)") },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    )
}

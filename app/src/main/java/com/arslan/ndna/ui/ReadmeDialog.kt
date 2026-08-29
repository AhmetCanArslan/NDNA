package com.arslan.ndna.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.arslan.ndna.model.Preview

/** README preview: the card opens this first, GitHub only on request. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadmeDialog(
    preview: Preview,
    onDismiss: () -> Unit,
    onBlock: () -> Unit,
    onOpen: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxWidth().padding(20.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxHeight(0.9f),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp
        ) {
            Column {
                PreviewHeader(preview)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                PreviewContent(preview, Modifier.weight(1f))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                PreviewActions(onDismiss, onBlock, onOpen)
            }
        }
    }
}

@Composable
private fun PreviewHeader(preview: Preview) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIcon(preview.item)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                preview.item.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                preview.item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        preview.item.stars?.let { StarBadge(it) }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PreviewContent(preview: Preview, modifier: Modifier) {
    Box(modifier.fillMaxWidth()) {
        when {
            preview.loading -> Centered { LoadingIndicator() }
            preview.error != null -> Centered {
                Text(
                    preview.error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> ReadmeHtml(preview.readme)
        }
    }
}

@Composable
private fun Centered(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) { content() }
}

@Composable
private fun PreviewActions(onDismiss: () -> Unit, onBlock: () -> Unit, onOpen: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onBlock) {
            Icon(Icons.Rounded.Block, null, Modifier.size(18.dp))
            Text("Block", Modifier.padding(start = 6.dp))
        }
        Box(Modifier.weight(1f))
        TextButton(onClick = onDismiss) { Text("Close") }
        Button(onClick = onOpen, shape = MaterialTheme.shapes.large) {
            Icon(Icons.AutoMirrored.Rounded.OpenInNew, null, Modifier.size(18.dp))
            Text("Open", Modifier.padding(start = 8.dp))
        }
    }
}

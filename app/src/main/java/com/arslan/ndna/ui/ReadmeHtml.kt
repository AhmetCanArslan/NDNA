package com.arslan.ndna.ui

import android.graphics.Color as AndroidColor
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView

/**
 * GitHub renders the README to HTML for us, so the preview only has to theme it
 * and hand it to a WebView. That keeps raw HTML in READMEs working.
 */
@Composable
fun ReadmeHtml(html: String, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val document = document(html, scheme.onSurface, scheme.primary, scheme.surfaceContainerHighest)
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                setBackgroundColor(AndroidColor.TRANSPARENT)
                isVerticalScrollBarEnabled = true
                settings.javaScriptEnabled = false
                settings.loadsImagesAutomatically = true
            }
        },
        update = { view ->
            view.loadDataWithBaseURL(BASE_URL, document, "text/html", "utf-8", null)
        }
    )
}

private fun document(html: String, text: Color, accent: Color, code: Color) = """
    <!doctype html>
    <html><head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style>
      body { margin:0; padding:20px; background:transparent; color:${css(text)};
             font-family:-apple-system, Roboto, sans-serif; font-size:15px; line-height:1.6; }
      h1,h2,h3,h4 { line-height:1.3; margin:20px 0 8px; }
      h1 { font-size:22px } h2 { font-size:19px } h3 { font-size:17px }
      a { color:${css(accent)}; text-decoration:none; }
      img { max-width:100%; height:auto; }
      pre, code { background:${css(code)}; border-radius:10px; font-size:13px; }
      code { padding:2px 5px; }
      pre { padding:12px; overflow-x:auto; }
      pre code { background:transparent; padding:0; }
      blockquote { margin:12px 0; padding:8px 14px; border-left:3px solid ${css(accent)};
                   border-radius:0 10px 10px 0; background:${css(code)}; }
      table { border-collapse:collapse; display:block; overflow-x:auto; }
      td, th { border:1px solid ${css(code)}; padding:6px 10px; }
      hr { border:none; border-top:1px solid ${css(code)}; margin:20px 0; }
    </style>
    </head><body>$html</body></html>
""".trimIndent()

private fun css(color: Color): String = "#%06X".format(0xFFFFFF and color.toArgb())

private const val BASE_URL = "https://github.com/"

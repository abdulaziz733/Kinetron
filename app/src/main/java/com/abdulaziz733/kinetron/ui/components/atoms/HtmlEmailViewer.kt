package com.abdulaziz733.kinetron.ui.components.atoms

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Leaf node Webview component to render raw styled HTML contents dynamically without vertical cutoff.
 */
@Composable
fun HtmlEmailViewer(
    htmlContent: String,
    modifier: Modifier = Modifier
) {
    if (LocalInspectionMode.current) {
        // Render a placeholder box in Compose IDE Previews to prevent Layoutlib WebView stub crashes
        Box(
            modifier = modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "HTML Content Preview Placeholder 📧\n\nRaw Content: $htmlContent",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    } else {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.supportZoom()
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false
                    
                    webViewClient = WebViewClient()
                }
            },
            update = { webView ->
                val styledHtml = if (htmlContent.contains("<meta name=\"viewport\"", ignoreCase = true)) {
                    htmlContent
                } else {
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\">$htmlContent"
                }
                webView.loadDataWithBaseURL(null, styledHtml, "text/html", "utf-8", null)
            },
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HtmlEmailViewerPreview() {
    MaterialTheme {
        HtmlEmailViewer(
            htmlContent = "<h3>Hello World</h3><p>This is a custom test HTML email representation.</p>"
        )
    }
}



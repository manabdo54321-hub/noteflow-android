package com.noteflow.app.features.graph.presentation

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel

private val BgColor = Color(0xFF131313)

class GraphBridge(
    private val onNodeClick: (String) -> Unit
) {
    @JavascriptInterface
    fun onNodeClick(nodeId: String) { onNodeClick(nodeId) }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GraphScreen(
    onBack: () -> Unit,
    onNoteClick: (Long) -> Unit = {},
    viewModel: GraphViewModel = hiltViewModel()
) {
    val graphJson by viewModel.graphJson.collectAsState()
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isLoaded  by remember { mutableStateOf(false) }

    LaunchedEffect(graphJson, isLoaded) {
        if (isLoaded && graphJson.isNotEmpty()) {
            val escaped = graphJson
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "")
                .replace("\r", "")
            webViewRef?.evaluateJavascript("loadData('$escaped')", null)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled  = true
                    addJavascriptInterface(
                        GraphBridge { nodeId ->
                            if (nodeId.startsWith("note_")) {
                                val id = nodeId.removePrefix("note_").toLongOrNull()
                                if (id != null) onNoteClick(id)
                            }
                            viewModel.setFocusedNode(nodeId)
                        },
                        "Android"
                    )
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView, url: String) {
                            isLoaded = true
                        }
                    }
                    loadUrl("file:///android_asset/graph.html")
                    webViewRef = this
                }
            }
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}
package com.noteflow.app.features.notes.presentation.screens

import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class NoteFlowBridge(
    private val onContentChanged: (String) -> Unit
) {
    @JavascriptInterface
    fun onContentChanged(text: String) {
        onContentChanged(text)
    }
}

@Composable
fun NoteEditorWebView(
    content: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isReady by remember { mutableStateOf(false) }

    LaunchedEffect(isReady) {
        if (isReady && content.isNotBlank()) {
            val escaped = content
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "")
            webViewRef?.evaluateJavascript("setContent('$escaped')", null)
        }
    }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                addJavascriptInterface(NoteFlowBridge(onContentChange), "NoteFlowBridge")
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        isReady = true
                        view?.evaluateJavascript("focusEditor()", null)
                    }
                }
                loadUrl("file:///android_asset/editor.html")
                webViewRef = this
            }
        },
        modifier = modifier
    )
}

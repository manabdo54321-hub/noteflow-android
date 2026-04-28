package com.noteflow.app.features.graph.presentation

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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val BgColor = Color(0xFF131313)
private val PrimaryColor = Color(0xFFCABEFF)

@Composable
fun GraphScreen(
    onBack: () -> Unit,
    viewModel: GraphViewModel = hiltViewModel()
) {
    val nodes by viewModel.nodes.collectAsState()
    val edges by viewModel.edges.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(BgColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp).statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = PrimaryColor)
            }
            Text("Graph View", color = PrimaryColor, fontSize = 18.sp)
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "${nodes.size} nodes | ${edges.size} edges",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
        }
    }
}

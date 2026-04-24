package com.noteflow.app.features.world.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.sceneview.SceneView
import io.github.sceneview.node.ModelNode

val BgColor = Color(0xFF131313)
val PrimaryColor = Color(0xFFCABEFF)
val SurfaceColor = Color(0xFF1C1B1B)
val TextPrimary = Color(0xFFEEEEEE)
val TextSecondary = Color(0xFF999999)
val AccentColor = Color(0xFF8A70FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        WorldTreeView()
        WorldTopBar(onBack = onBack)
        WorldBottomInfo()
    }
}

@Composable
private fun WorldTreeView() {
    val context = LocalContext.current
    AndroidView(
        factory = { ctx ->
            SceneView(ctx).apply {
                // تحميل الشجرة
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { sceneView ->
            sceneView.modelLoader?.let { loader ->
                loader.loadModelGlbAsync(
                    glbFileLocation = "fantasy_tree.glb",
                    onResult = { modelInstance ->
                        val node = ModelNode(
                            modelInstance = modelInstance,
                            scaleToUnits = 2.0f
                        )
                        sceneView.addChildNode(node)
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorldTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "عالم البناء",
                color = PrimaryColor,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "رجوع",
                    tint = PrimaryColor
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BgColor.copy(alpha = 0.8f)
        )
    )
}

@Composable
private fun WorldBottomInfo() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceColor
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "شجرة حياتك",
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "كل هدف تحققه يجعل شجرتك تنمو",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = 0.1f,
                    modifier = Modifier.fillMaxWidth(),
                    color = AccentColor,
                    trackColor = SurfaceColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "المستوى 1 — بذرة",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

package com.noteflow.app.features.intro.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val BgColor = Color(0xFF0E0E0E)
private val PrimaryColor = Color(0xFFCABEFF)
private val SecondaryColor = Color(0xFF75D1FF)
private val AccentColor = Color(0xFF8A70FF)
private val OnSurfaceVariant = Color(0xFFC8C5CD)
private val OnPrimaryFixed = Color(0xFF1C0062)

@Composable
fun IntroScreen(onFinished: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }

    LaunchedEffect(currentPage) {
        delay(2500)
        if (currentPage == 0) {
            currentPage = 1
        } else {
            onFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor),
        contentAlignment = Alignment.Center
    ) {
        if (currentPage == 0) PageOne() else PageTwo()

        // مؤشر الصفحات
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(2) { index ->
                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .width(if (index == currentPage) 32.dp else 16.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (index == currentPage)
                                Brush.horizontalGradient(listOf(PrimaryColor, AccentColor))
                            else
                                Brush.horizontalGradient(listOf(
                                    OnSurfaceVariant.copy(alpha = 0.3f),
                                    OnSurfaceVariant.copy(alpha = 0.3f)
                                ))
                        )
                )
            }
        }
    }
}

@Composable
private fun PageOne() {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(400.dp)
        )
        Box(
            modifier = Modifier
                .size(280.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(Color.White.copy(alpha = 0.03f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(110.dp)
                )
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(listOf(PrimaryColor, AccentColor))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Notes, contentDescription = null,
                        tint = OnPrimaryFixed, modifier = Modifier.size(40.dp))
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "NoteFlow",
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold,
                    style = LocalTextStyle.current.copy(
                        brush = Brush.linearGradient(listOf(PrimaryColor, AccentColor))
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.width(32.dp).height(1.dp)
                        .background(OnSurfaceVariant.copy(alpha = 0.2f)))
                    Text("THE DIGITAL CURATOR", fontSize = 10.sp,
                        fontWeight = FontWeight.Medium, letterSpacing = 3.sp,
                        color = OnSurfaceVariant.copy(alpha = 0.4f))
                    Box(modifier = Modifier.width(32.dp).height(1.dp)
                        .background(OnSurfaceVariant.copy(alpha = 0.2f)))
                }
            }
        }

        // زاوية الديكور
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(48.dp)
        ) {
            Box(
                modifier = Modifier.width(1.dp).height(64.dp)
                    .background(Brush.verticalGradient(
                        listOf(PrimaryColor.copy(alpha = 0.2f), Color.Transparent)))
            )
            Box(
                modifier = Modifier.width(64.dp).height(1.dp).offset(y = 63.dp)
                    .background(Brush.horizontalGradient(
                        listOf(Color.Transparent, PrimaryColor.copy(alpha = 0.2f))))
            )
        }
    }
}

@Composable
private fun PageTwo() {
    val infiniteTransition = rememberInfiniteTransition(label = "glow2")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow2"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Notes, contentDescription = null,
                tint = PrimaryColor, modifier = Modifier.size(20.dp))
            Text("NoteFlow", fontWeight = FontWeight.Bold,
                color = Color.White, fontSize = 18.sp)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Text("Focus. ", fontSize = 38.sp,
                    fontWeight = FontWeight.Bold, color = Color.White)
                Text("Think.", fontSize = 38.sp,
                    fontWeight = FontWeight.Bold, color = PrimaryColor)
            }
            Text("Build.", fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = SecondaryColor.copy(alpha = 0.7f))
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            Text("PREPARING YOUR CREATIVE SPACE",
                fontSize = 10.sp, letterSpacing = 2.sp,
                color = OnSurfaceVariant.copy(alpha = 0.4f))
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2A2A2A)),
                contentAlignment = Alignment.Center
            ) {
                Text("✦", fontSize = 24.sp, color = PrimaryColor)
            }
        }
    }
}

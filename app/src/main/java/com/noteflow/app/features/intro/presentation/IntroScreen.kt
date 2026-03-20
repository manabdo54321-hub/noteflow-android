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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// الألوان
private val BackgroundColor = Color(0xFF0E0E0E)
private val PrimaryColor = Color(0xFFCABEFF)
private val SecondaryColor = Color(0xFF75D1FF)
private val SurfaceColor = Color(0xFF201F1F)
private val OnSurfaceVariant = Color(0xFFC8C5CD)

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
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (currentPage == 0) {
            PageOne()
        } else {
            PageTwo()
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // التوهج الخلفي الكبير
        Box(
            modifier = Modifier
                .size(400.dp)
                .blur(120.dp)
                .background(
                    PrimaryColor.copy(alpha = glowAlpha * 0.1f),
                    CircleShape
                )
        )

        // التوهج الثاني
        Box(
            modifier = Modifier
                .size(280.dp)
                .blur(100.dp)
                .background(
                    SecondaryColor.copy(alpha = glowAlpha * 0.08f),
                    CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // الأيقونة مع التوهج
            Box(contentAlignment = Alignment.Center) {
                // الحلقة الخارجية
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .background(
                            SurfaceColor.copy(alpha = 0.3f),
                            CircleShape
                        )
                )
                // التوهج حول الأيقونة
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .blur(20.dp)
                        .background(
                            PrimaryColor.copy(alpha = glowAlpha * 0.4f),
                            CircleShape
                        )
                )
                // الأيقونة نفسها
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryColor, Color(0xFF8A70FF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Notes,
                        contentDescription = null,
                        tint = BackgroundColor,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // النص
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "NoteFlow",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    style = LocalTextStyle.current.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryColor, Color(0xFF8A70FF))
                        )
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(1.dp)
                            .background(OnSurfaceVariant.copy(alpha = 0.2f))
                    )
                    Text(
                        text = "THE DIGITAL CURATOR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 3.sp,
                        color = OnSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(1.dp)
                            .background(OnSurfaceVariant.copy(alpha = 0.2f))
                    )
                }
            }
        }

        // زاوية الديكور
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(48.dp)
                .size(64.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(64.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(PrimaryColor.copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(1.dp)
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, PrimaryColor.copy(alpha = 0.2f))
                        )
                    )
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // التوهج الخلفي
        Box(
            modifier = Modifier
                .size(350.dp)
                .blur(100.dp)
                .background(
                    PrimaryColor.copy(alpha = glowAlpha * 0.08f),
                    CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Focus. Think. Build.
            Row {
                Text(
                    text = "Focus. ",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Think.",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }
            Text(
                text = "Build.",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = SecondaryColor.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "PREPARING YOUR CREATIVE SPACE",
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = OnSurfaceVariant.copy(alpha = 0.4f)
            )
        }

        // أيقونة النجوم في الأسفل
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceColor),
                contentAlignment = Alignment.Center
            ) {
                Text("✦", fontSize = 24.sp, color = PrimaryColor)
            }
        }
    }
}

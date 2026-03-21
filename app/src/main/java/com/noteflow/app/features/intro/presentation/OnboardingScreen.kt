package com.noteflow.app.features.intro.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Sync
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

private val BgColor = Color(0xFF0E0E0E)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val OnSurfaceVariant = Color(0xFFC8C5CD)
private val OnPrimaryFixed = Color(0xFF1C0062)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        when (currentPage) {
            0 -> WelcomePage(onGetStarted = { currentPage = 1 })
            1 -> SyncPage(
                onSkip = { onFinished() },
                onContinue = { onFinished() }
            )
        }

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
private fun WelcomePage(onGetStarted: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomStart)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 80.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(PrimaryColor, AccentColor))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Notes, contentDescription = null,
                        tint = OnPrimaryFixed, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("NoteFlow", fontSize = 24.sp,
                    fontWeight = FontWeight.Bold, color = Color.White)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "مرحباً بك في NoteFlow",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 44.sp
                )
                Text(
                    text = "نظّم أفكارك. أدِر حياتك.",
                    fontSize = 18.sp,
                    color = OnSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(listOf(PrimaryColor, AccentColor)),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("ابدأ الآن", fontSize = 16.sp,
                            fontWeight = FontWeight.Bold, color = OnPrimaryFixed)
                    }
                }
            }
        }
    }
}

@Composable
private fun SyncPage(onSkip: () -> Unit, onContinue: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopStart)
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Notes, contentDescription = null, tint = PrimaryColor)
            Text("NoteFlow", fontWeight = FontWeight.Bold,
                color = Color.White, fontSize = 20.sp)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Box(modifier = Modifier.size(240.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                )
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(SurfaceHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null,
                        tint = PrimaryColor, modifier = Modifier.size(72.dp))
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceHigh),
                    contentAlignment = Alignment.Center
                ) { Text("☁️", fontSize = 18.sp) }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceHigh),
                    contentAlignment = Alignment.Center
                ) { Text("📱", fontSize = 20.sp) }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("زامن بياناتك", fontSize = 34.sp,
                fontWeight = FontWeight.Bold, color = Color.White,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))
            Text("اوصل لملاحظاتك ومهامك في أي وقت وأي مكان.",
                fontSize = 16.sp, color = OnSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(listOf(PrimaryColor, AccentColor)),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Text("G", fontWeight = FontWeight.Bold,
                                color = OnPrimaryFixed, fontSize = 18.sp)
                            Text("المتابعة مع Google",
                                fontWeight = FontWeight.Bold,
                                color = OnPrimaryFixed, fontSize = 15.sp)
                        }
                    }
                }

                OutlinedButton(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceHigh)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("🔄", fontSize = 18.sp)
                        Text("المزامنة مع Google Drive",
                            color = Color.White, fontSize = 15.sp)
                    }
                }

                TextButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
                    Text("تخطي في الوقت الحالي",
                        color = OnSurfaceVariant, fontSize = 14.sp)
                }
            }
        }
    }
}

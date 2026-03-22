package com.noteflow.app.features.settings.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BgColor = Color(0xFF131313)
private val SurfaceColor = Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val OnSurfaceVariant = Color(0xFFC8C5CD)
private val ErrorColor = Color(0xFFFF6B6B)

@Composable
private fun SettingsDivider() {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(1.dp).background(Color(0xFF47464C).copy(alpha = 0.3f)))
}

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName
    var notificationsEnabled by remember { mutableStateOf(true) }
    var offlineStorage by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(0) }

    LazyColumn(modifier = Modifier.fillMaxSize().background(BgColor), contentPadding = PaddingValues(bottom = 32.dp)) {
        item { SettingsHeader() }
        item { SettingsProfileCard() }
        item { SettingsSectionLabel("المظهر") }
        item { SettingsThemeCard(selectedTheme) { selectedTheme = it } }
        item { SettingsSectionLabel("الوظائف") }
        item { SettingsFunctionalCard(notificationsEnabled, offlineStorage, { notificationsEnabled = it }, { offlineStorage = it }) }
        item { SettingsSectionLabel("النظام") }
        item { SettingsSystemCard() }
        item { SettingsVersionFooter(versionName) }
    }
}

@Composable
private fun SettingsHeader() {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp).statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = OnSurfaceVariant)
        Text("الإعدادات", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        Icon(Icons.Default.Search, contentDescription = null, tint = OnSurfaceVariant)
    }
}

@Composable
private fun SettingsSectionLabel(label: String) {
    Text(label, fontSize = 11.sp, letterSpacing = 2.sp, color = PrimaryColor, fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
}

@Composable
private fun SettingsProfileCard() {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceColor)) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Brush.linearGradient(listOf(PrimaryColor, AccentColor))), contentAlignment = Alignment.Center) {
                Text("م", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C0062))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("مستخدم NoteFlow", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                Text("noteflow@app.io", fontSize = 13.sp, color = OnSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(AccentColor.copy(alpha = 0.2f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(12.dp))
                        Text("عضو مميز", fontSize = 11.sp, color = PrimaryColor)
                    }
                }
            }
            Text("تعديل", fontSize = 13.sp, color = PrimaryColor, modifier = Modifier.clickable { })
        }
    }
}

@Composable
private fun SettingsThemeCard(selectedTheme: Int, onThemeChange: (Int) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceColor)) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Palette, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
                Column {
                    Text("السمة", color = Color.White, fontSize = 15.sp)
                    Text("الوضع الداكن نشط", fontSize = 12.sp, color = OnSurfaceVariant)
                }
            }
            Row(modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(SurfaceHigh).padding(4.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                listOf(Icons.Default.DarkMode to "داكن", Icons.Default.LightMode to "فاتح", Icons.Default.BrightnessAuto to "تلقائي")
                    .forEachIndexed { index, (icon, label) ->
                        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTheme == index) PrimaryColor else Color.Transparent)
                            .clickable { onThemeChange(index) }, contentAlignment = Alignment.Center) {
                            Icon(icon, contentDescription = label,
                                tint = if (selectedTheme == index) Color(0xFF1C0062) else OnSurfaceVariant,
                                modifier = Modifier.size(16.dp))
                        }
                    }
            }
        }
    }
}

@Composable
private fun SettingsFunctionalCard(notificationsEnabled: Boolean, offlineStorage: Boolean, onNotificationsChange: (Boolean) -> Unit, onOfflineChange: (Boolean) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceColor)) {
        Column {
            SettingsToggleRow(Icons.Default.Notifications, "الإشعارات", "إشعارات فورية وصوتية", notificationsEnabled, onNotificationsChange)
            SettingsDivider()
            SettingsArrowRow(Icons.Default.Sync, "مزامنة البيانات", "آخر مزامنة منذ دقيقتين")
            SettingsDivider()
            SettingsToggleRow(Icons.Default.Storage, "التخزين بدون إنترنت", "احتفظ بآخر 100 ملاحظة", offlineStorage, onOfflineChange)
        }
    }
}

@Composable
private fun SettingsSystemCard() {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceColor)) {
        Column {
            SettingsArrowRow(Icons.Default.Security, "الأمان وكلمة المرور", null)
            SettingsDivider()
            SettingsArrowRow(Icons.Default.Help, "المساعدة والدعم", null, isExternal = true)
            SettingsDivider()
            Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = ErrorColor, modifier = Modifier.size(20.dp))
                Text("تسجيل الخروج", color = ErrorColor, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun SettingsVersionFooter(versionName: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(SurfaceColor), contentAlignment = Alignment.Center) {
            Text("✦", fontSize = 20.sp, color = PrimaryColor)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("NOTEFLOW V$versionName-STABLE", fontSize = 11.sp, letterSpacing = 2.sp, color = OnSurfaceVariant)
        Text("صُنع بدقة تحريرية", fontSize = 11.sp, color = OnSurfaceVariant.copy(alpha = 0.5f))
    }
}

@Composable
private fun SettingsToggleRow(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
            Column {
                Text(title, color = Color.White, fontSize = 15.sp)
                Text(subtitle, fontSize = 12.sp, color = OnSurfaceVariant)
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF1C0062), checkedTrackColor = PrimaryColor,
                uncheckedThumbColor = OnSurfaceVariant, uncheckedTrackColor = Color(0xFF2A2A2A)))
    }
}

@Composable
private fun SettingsArrowRow(icon: ImageVector, title: String, subtitle: String?, isExternal: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
            Column {
                Text(title, color = Color.White, fontSize = 15.sp)
                if (subtitle != null) Text(subtitle, fontSize = 12.sp, color = OnSurfaceVariant)
            }
        }
        Icon(if (isExternal) Icons.Default.OpenInNew else Icons.Default.ChevronRight,
            contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
    }
}

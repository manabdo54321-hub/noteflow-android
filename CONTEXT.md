# NoteFlow - Android App

## معلومات المشروع
- **الاسم:** NoteFlow
- **Package:** com.noteflow.app
- **GitHub:** manabdo54321-hub/noteflow-android
- **Branch:** main

## التقنيات
- Kotlin + Jetpack Compose
- Room (قاعدة البيانات)
- Hilt (Dependency Injection)
- Navigation Compose
- Coroutines + Flow
- Material Icons Extended
- SensorManager (Accelerometer للوضع الصارم)
- RingtoneManager (جرس التايمر)
- NotificationManager (DND للوضع الصارم)

## إعدادات البناء (مهم جداً — لا تغير)
- **minSdk:** 26
- **compileSdk:** 34
- **targetSdk:** 34
- **jvmTarget:** 11
- **sourceCompatibility:** JavaVersion.VERSION_11
- **targetCompatibility:** JavaVersion.VERSION_11
- **Compose BOM:** 2023.08.00
- **Kotlin Compiler Extension:** 1.5.8
- **AppCompat:** 1.6.1

## Permissions في AndroidManifest
- WRITE_EXTERNAL_STORAGE
- VIBRATE
- USE_EXACT_ALARM
- ACCESS_NOTIFICATION_POLICY

## إعدادات الـ Theme
- **themes.xml:** parent="Theme.AppCompat.DayNight.NoActionBar"
- لا تستخدم parent="android:Theme.DeviceDefault"
- **windowSoftInputMode:** adjustResize

## قواعد Android 11
1. VerifyError — أي Composable أكبر من ~150 سطر → قسّم
2. Modifier.blur() — مش موجود على Android 11
3. CompositingStrategy.Offscreen — API 31+ فقط
4. Compose BOM 2024+ — لا تستخدم
5. jvmTarget = "17" — بيعمل VerifyError

## قواعد الكود العامة
- ملف واحد في كل مرة
- الألوان: BgColor=#131313, SurfaceColor=#1C1B1B, SurfaceHigh=#2A2A2A, PrimaryColor=#CABEFF, AccentColor=#8A70FF, TertiaryColor=#75D1FF
- استخدم SimpleDivider أو SettingsDivider (custom Box)
- أي Composable أكبر من 150 سطر — قسّمها
- استخدم TextFieldValue بدل String لأي TextField محتاج cursor control

## هيكل التطبيق

### Navigation
- مفيش Bottom Navigation Bar
- TimerViewModel بيتشارك على مستوى AppNavigation
- Routes: home, notes, note/{noteId}, tasks, timer, stats, settings, search, ai, world, tags

## المراحل المكتملة
✅ المرحلة 0 — Build شغال
✅ المرحلة 1 — قاعدة البيانات
✅ المرحلة 2 — شاشات الملاحظات
✅ المرحلة 3 — المهام والبومودورو
✅ المرحلة 4 — الربط والذكاء
✅ المرحلة 5 — إصلاح الـ crash على Android 11
✅ المرحلة 5.5 — تقسيم كل الشاشات
✅ المرحلة 6 — HomeScreen (Zen Mode + Obsidian + Bottom Sheet)
✅ المرحلة 7 — TimerScreen كامل
✅ المرحلة 8.5 — SmartWrite
✅ المرحلة 9 — ObsidianToolbar محسّن
✅ المرحلة 9.5 — Tags Integration (جزئي)

## Tags System — الوضع الحالي

### المكتمل ✅
- Tag.kt, TagRepository.kt, TagExtractor.kt
- TagEntity, NoteTagCrossRef, TaskTagCrossRef, GoalTagCrossRef
- TagDao, AppDatabase migration 4→5
- TagRepositoryImpl
- TagViewModel, TagSuggestionDropdown, TagDashboardScreen
- HomeScreen ← TagSuggestionDropdown في QuickWrite ✅
- TaskListScreen ← TagFilterBar مضافة ✅ (بس فيها مشكلة)

### المشكلة الحالية ⚠️
TaskListScreen فيها filter بيعمل:
task.tags.contains(selectedTagId.toString())
لكن Task model مفيش فيه tags field خالص!
محتاج نصلح الفلترة عن طريق TagRepository مش Task model

### الحل المطلوب
- استخدام خيار 2: جيب المهام المرتبطة بتاج من TagRepository
- TagDao فيه getTasksByTag أو TaskTagCrossRef
- TaskListScreen تستخدم tagViewModel.getTaskIdsByTag(tagId)

### الناقص في Tags Integration
- TaskListScreen filter — إصلاح المشكلة ⚠️
- NoteDetailScreen ← suggestions + highlight
- GoalsScreen ← مفيش ملف خالص، محتاج يتعمل من الصفر

## الناقص (الأولوية بالترتيب)
1. 🔴 إصلاح TaskListScreen tag filter
2. 🔴 GoalsScreen — من الصفر
3. 🟠 NoteDetailScreen ← tag suggestions + highlight
4. 🟡 SearchScreen حقيقي
5. 🟡 الضوضاء البيضاء — ملفات mp3
6. 🟢 AI Integration بـ Groq
7. 🟢 Graph View
8. 🟢 Export PDF
9. 🔵 مسح crash logger من NoteFlowApp

## الخطوة الجاية
إصلاح TaskListScreen tag filter:
1. شوف TagDao — هل فيه getTaskIdsByTag؟
2. لو موجود → استخدمه في TagViewModel
3. لو مش موجود → نضيفه في TagDao
4. نعدّل TaskListScreen تستخدم tagViewModel بشكل صح

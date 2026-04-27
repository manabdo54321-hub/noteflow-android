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
6. DatePickerDefaults.colors — لا تستخدم navigationContentColor (مش موجود في BOM 2023.08.00)

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
- Routes: home, notes, note/{noteId}, tasks, timer, stats, settings, search, ai, world, tags, goals

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
✅ المرحلة 10 — إصلاح TaskListScreen tag filter
✅ المرحلة 11 — GoalsScreen من الصفر

## Goals System — مكتمل ✅
- Goal.kt — domain model
- GoalEntity.kt — Room entity
- GoalDao.kt — CRUD + Flow
- GoalRepository interface + GoalRepositoryImpl
- AppDatabase version=6 + migration goals table
- AppModule — GoalDao + GoalRepository
- GoalViewModel — allGoals, showAddDialog, editingGoal, saveGoal, updateProgress, toggleComplete, deleteGoal
- GoalsScreen.kt — main screen
- GoalsComponents.kt — GoalsHeader, GoalsTitle, GoalsSummaryRow, GoalCard, GoalsEmptyState, GoalsSectionLabel
- GoalAddEditDialog.kt — مع DatePicker (بدون navigationContentColor)
- GoalsUtils.kt — clickableNoRipple
- AppNavigation — route "goals" مضاف

## Tags System — مكتمل ✅
- Tag.kt, TagRepository.kt, TagExtractor.kt
- TagEntity, NoteTagCrossRef, TaskTagCrossRef, GoalTagCrossRef
- TagDao, AppDatabase migration 4→5
- TagRepositoryImpl
- TagViewModel — allTags, suggestions, selectedTagId, taskIdsByTag
- TagSuggestionDropdown, TagDashboardScreen
- HomeScreen ← TagSuggestionDropdown في QuickWrite ✅
- TaskListScreen ← TagFilterBar + فلترة عن طريق tagViewModel.taskIdsByTag ✅

## AppDatabase
- **version = 6**
- **fallbackToDestructiveMigration()** — عادي في مرحلة development
- entities: NoteEntity, TaskEntity, SessionEntity, AiChatEntity, TagEntity, NoteTagCrossRef, TaskTagCrossRef, GoalTagCrossRef, GoalEntity

## الناقص (الأولوية بالترتيب)
1. 🟠 NoteDetailScreen ← tag suggestions + highlight
2. 🟡 SearchScreen حقيقي
3. 🟡 الضوضاء البيضاء — ملفات mp3
4. 🟢 AI Integration بـ Groq
5. 🟢 Graph View
6. 🟢 Export PDF
7. 🔵 مسح crash logger من NoteFlowApp

## آخر حاجة وصلنا ليها
GoalsScreen اتعملت بالكامل وعمل build ✅
الخطوة الجاية: NoteDetailScreen ← tag suggestions + highlight

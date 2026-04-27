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
✅ المرحلة 10 — إصلاح TaskListScreen tag filter

## Tags System — الوضع الحالي

### المكتمل ✅
- Tag.kt, TagRepository.kt, TagExtractor.kt
- TagEntity, NoteTagCrossRef, TaskTagCrossRef, GoalTagCrossRef
- TagDao, AppDatabase migration 4→5
- TagRepositoryImpl
- TagViewModel, TagSuggestionDropdown, TagDashboardScreen
- HomeScreen ← TagSuggestionDropdown في QuickWrite ✅
- TaskListScreen ← TagFilterBar + فلترة صحيحة عن طريق tagViewModel.taskIdsByTag ✅

### TagViewModel — الوضع الحالي ✅
- allTags: StateFlow<List<Tag>>
- suggestions: StateFlow<List<Tag>>
- selectedTagId: StateFlow<Long?>
- taskIdsByTag: StateFlow<List<Long>> ← جديد، بيتحدث تلقائي لما selectedTagId يتغير
- selectTag(tagId) ← بيحدث selectedTagId وبالتالي taskIdsByTag

### TaskListScreen — الفلترة الحالية ✅
- بتستخدم tagViewModel.selectedTagId و tagViewModel.taskIdsByTag
- الفلترة: allActiveTasks.filter { task -> taskIdsByTag.contains(task.id) }
- مفيش أي reference لـ task.tags (اللي كان بيعمل compile error)

## Goals System — الوضع الحالي

### الموجود ✅
- GoalTagCrossRef.kt ← موجود في tags/data/local
- goal_tag_cross_ref table ← موجودة في migration 4→5

### الناقص ❌ — محتاج يتبنى من الصفر
- Goal.kt (domain model)
- GoalEntity.kt (Room entity)
- GoalDao.kt
- GoalRepository interface
- GoalRepositoryImpl
- GoalViewModel
- GoalsScreen (مقسمة لملفات صغيرة)
- AppDatabase migration 5→6 (إضافة جدول goals)
- Route في Navigation

### AppDatabase — الوضع الحالي
- **version = 5**
- entities: NoteEntity, TaskEntity, SessionEntity, AiChatEntity, TagEntity, NoteTagCrossRef, TaskTagCrossRef, GoalTagCrossRef
- migration 5→6 محتاج يضيف: CREATE TABLE goals

### Goal Model المقترح (على نفس pattern Task)
- id: Long
- title: String
- description: String
- isCompleted: Boolean
- progress: Int (0-100)
- targetDate: Long?
- createdAt: Long

## الناقص (الأولوية بالترتيب)
1. 🔴 GoalsScreen — من الصفر (الخطوة الجاية)
2. 🟠 NoteDetailScreen ← tag suggestions + highlight
3. 🟡 SearchScreen حقيقي
4. 🟡 الضوضاء البيضاء — ملفات mp3
5. 🟢 AI Integration بـ Groq
6. 🟢 Graph View
7. 🟢 Export PDF
8. 🔵 مسح crash logger من NoteFlowApp

## الخطوة الجاية — GoalsScreen من الصفر
الترتيب:
1. Goal.kt — domain model
2. GoalEntity.kt — Room entity + toDomain/fromDomain
3. GoalDao.kt — CRUD + Flow
4. GoalRepository.kt — interface
5. GoalRepositoryImpl.kt — implementation + Hilt
6. AppDatabase.kt — version 6 + migration 5→6 (إضافة جدول goals) + goalDao()
7. GoalViewModel.kt
8. GoalsScreen.kt — مقسمة لـ Composables صغيرة

## آخر حاجة وصلنا ليها
إصلاح TaskListScreen tag filter — اتعمل وعمل push ✅
الـ build نجح ✅
جاهزين نبدأ GoalsScreen من الصفر بداية من Goal.kt

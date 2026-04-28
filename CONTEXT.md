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

## Obsidian Toolbar System — مكتمل جزئياً

### الملفات
- ObsidianTextEngine.kt ✅ — كل الـ text logic
- ObsidianToolbar.kt ✅ — الـ UI + selection fix
- MarkdownEngine.kt ✅ — Visual Transformation

### إصلاحات مهمة تمت
- KEYBOARD_TAP crash → استبدل بـ VIRTUAL_KEY ✅
- حذف ObsidianToolbar القديم من HomeScreen ✅
- Selection fix بـ savedValue + LaunchedEffect ✅
- MarkdownEngine H1-H6 + inline styles + tags + checkboxes ✅
- MarkdownVisualTransformation.kt — محذوف (كان متعارض)
- MarkdownInlineStyles.kt — محذوف (كان متعارض)

### أزرار الـ Toolbar الحالية ✅
- Bold → **text**
- Italic → *text*
- Heading H1/H2/H3 → # / ## / ###
- Bullet List → -
- Numbered List → 1.
- Checkbox → - [ ]
- Code inline → `text`
- Code Block → ```
- Quote → >
- Wiki Link → [[text]]
- Highlight → ==text==
- Strikethrough → ~~text~~
- Table → | col |
- Tag → #
- Undo/Redo ✅
- Indent/Unindent ✅

### ناقص في الـ Toolbar
- H4/H5/H6 أزرار (موجودة في Engine بس مش في الـ UI)
- External Link → [نص](رابط)
- Embed → ![[ملاحظة]]
- Callouts → > [!INFO]

### مراحل Obsidian Toolbar
✅ المرحلة 1 — إصلاح الـ Selection
✅ المرحلة 2 — Toolbar أزرار (جزئي — ناقص H4-H6 + External Link + Embed)
✅ المرحلة 3 — Preview Mode (MarkdownEngine)
🟠 المرحلة 4 — Callouts + Advanced

### مؤجل (مش أولوية)
- Canvas — محتاج library ضخمة
- LaTeX / Mermaid — محتاج WebView
- Properties/YAML — مش أولوية

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
1. 🟠 المرحلة 4 Obsidian — Callouts + Advanced (H4-H6 + External Link + Embed)
2. 🟠 NoteDetailScreen ← tag suggestions + highlight
3. 🟡 SearchScreen حقيقي
4. 🟡 الضوضاء البيضاء — ملفات mp3
5. 🟢 AI Integration بـ Groq
6. 🟢 Graph View
7. 🟢 Export PDF
8. 🔵 مسح crash logger من NoteFlowApp

## آخر حاجة وصلنا ليها
- MarkdownEngine H1-H6 + Tags coloring ✅
- Build نجح ✅
- الخطوة الجاية: المرحلة 4 Obsidian — Callouts + Advanced

## قواعد العمل من Termux
- الملفات بتتكتب بـ cat > file << 'EOF'
- التعديلات الصغيرة (سطرين/ثلاثة) → nano مباشرة
- nano 9.0 مثبت ✅
- كل ملف 100-150 سطر max
- لا تستخدم printf مع نصوص عربية
- الرفع دايماً: git add -A && git commit -m "..." && git push
- GitHub Actions هو اللي يبني — مش Termux
- لو فشل البناء: اقرأ السطر الأول من الخطأ بس

## طريقة التعامل مع Claude
- في أول كل محادثة جديدة: ابعت محتوى CONTEXT.md
- بعدين قول: "اقرا CONTEXT.md وابدأ من الخطوة الجاية"
- قبل أي كود: Claude لازم يشوف الملف الحالي الأول
- Claude مش بيبدأ أي تعديل إلا بموافقتك
- لو الملف كبير: بيتقسم لملفات صغيرة من الأول

## الخطة المعتمدة
- الخطة المعتمدة هي NoteFlow_Guide_v2
- مسار الملفات الجديدة للـ Goals:
  features/goals/presentation/GoalsUtils.kt (مش utils/)

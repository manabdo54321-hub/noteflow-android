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

## إعدادات البناء (مهم جداً لا تغير)
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
1. VerifyError - اي Composable اكبر من 150 سطر - قسم
2. Modifier.blur() - مش موجود على Android 11
3. CompositingStrategy.Offscreen - API 31+ فقط
4. Compose BOM 2024+ - لا تستخدم
5. jvmTarget = "17" - بيعمل VerifyError
6. DatePickerDefaults.colors - لا تستخدم navigationContentColor (مش موجود في BOM 2023.08.00)
7. HorizontalDivider - غير موجود في BOM 2023.08.00 - استخدم Box بدلها
8. padding(bottom=) في Modifier - غير صحيح - استخدم offset(y=(-X).dp) بدلها
9. لا تستخدم Divider - استخدم Box(height=1.dp)

## قواعد الكود العامة
- ملف واحد في كل مرة
- الالوان: BgColor=#131313, SurfaceColor=#1C1B1B, SurfaceHigh=#2A2A2A, PrimaryColor=#CABEFF, AccentColor=#8A70FF, TertiaryColor=#75D1FF
- استخدم SimpleDivider او SettingsDivider (custom Box)
- اي Composable اكبر من 150 سطر - قسمها
- استخدم TextFieldValue بدل String لاي TextField محتاج cursor control

## هيكل التطبيق

### Navigation
- مفيش Bottom Navigation Bar
- TimerViewModel بيتشارك على مستوى AppNavigation
- Routes: home, notes, note/{noteId}, tasks, timer, stats, settings, search, ai, world, tags, goals, graph

## المراحل المكتملة
- المرحلة 0  - Build شغال
- المرحلة 1  - قاعدة البيانات
- المرحلة 2  - شاشات الملاحظات
- المرحلة 3  - المهام والبومودورو
- المرحلة 4  - الربط والذكاء
- المرحلة 5  - اصلاح الـ crash على Android 11
- المرحلة 5.5 - تقسيم كل الشاشات
- المرحلة 6  - HomeScreen (Zen Mode + Obsidian + Bottom Sheet)
- المرحلة 7  - TimerScreen كامل
- المرحلة 8.5 - SmartWrite
- المرحلة 9  - ObsidianToolbar محسن
- المرحلة 9.5 - Tags Integration (جزئي)
- المرحلة 10 - اصلاح TaskListScreen tag filter
- المرحلة 11 - GoalsScreen من الصفر
- المرحلة 12 - Obsidian Toolbar المرحلة 4 (Callouts + Advanced)
- المرحلة 13 - NoteDetailScreen Tags
- المرحلة 14A - Graph Data Layer
- المرحلة 14B - Physics Engine (Verlet + Vector Forces)
- المرحلة 14C - GraphScreen + GraphRenderer + RenderModels + Graph navigation
- المرحلة 15 - اصلاح OffsetMapping crash في MarkdownEngine
- المرحلة 16 - تنظيف الكود الميت
- المرحلة 17 - CrashLogScreen

## Obsidian Toolbar System - مكتمل

### المسارات الصحيحة للملفات
- ui/components/ObsidianTextEngine.kt
- ui/components/ObsidianToolbar.kt
- ui/components/MarkdownEngine.kt

### ازرار الـ Toolbar الحالية
- Undo / Redo
- H1 H6 Dropdown (زرار واحد يفتح قائمة)
- Bold
- Italic
- Strikethrough
- Highlight
- Inline Code
- Bullet List
- Numbered List
- Checkbox
- Blockquote
- Code Block
- Table
- Horizontal Rule
- Callout
- Wiki Link
- Embed
- External Link
- Tag
- Indent / Unindent

### MarkdownEngine - Rendering مكتمل
- H1 H6 باحجام مختلفة
- Bold, Italic, Strikethrough, Highlight
- Inline Code, Code Block
- Blockquote عادي
- Horizontal Rule
- Checkboxes
- Bullet List, Numbered List
- Callouts بالوان: INFO WARNING TIP DANGER QUESTION NOTE
- Wiki Link بلون بنفسجي + underline
- Embed بلون تركواز + underline
- External Link بلون ازرق + underline
- Tags بلون بنفسجي
- Hidden Comment - بيختفي في الـ rendering

### اخطاء حدثت وتم حلها
- MarkdownEngine.kt اتمسح بسبب nano - تم اعادة كتابته بـ cat
- UnicodeEncodeError في Python بسبب emoji - تم استبداله بنص عادي
- grep bash error مع ![[  - بسبب bash history expansion
- HorizontalDivider غير موجود في BOM 2023.08.00 - استخدم Box
- padding(bottom=) في Modifier غير صحيح - استخدم offset(y=)
- toArgb() في GraphRenderer كان خطا - تم حذفه
- HomeLeftDrawer params كانت ناقصة onNavigateToGraph

## Goals System - مكتمل
- Goal.kt - domain model
- GoalEntity.kt - Room entity
- GoalDao.kt - CRUD + Flow
- GoalRepository interface + GoalRepositoryImpl
- AppDatabase version=6 + migration goals table
- AppModule - GoalDao + GoalRepository
- GoalViewModel
- GoalsScreen.kt
- GoalsComponents.kt
- GoalAddEditDialog.kt
- GoalsUtils.kt
- AppNavigation - route "goals" مضاف

## Tags System - مكتمل
- Tag.kt, TagRepository.kt, TagExtractor.kt
- TagEntity, NoteTagCrossRef, TaskTagCrossRef, GoalTagCrossRef
- TagDao, AppDatabase migration 4 5
- TagRepositoryImpl
- TagViewModel - allTags, suggestions, selectedTagId, taskIdsByTag
- TagSuggestionDropdown, TagDashboardScreen
- HomeScreen - TagSuggestionDropdown في QuickWrite
- TaskListScreen - TagFilterBar + فلترة عن طريق tagViewModel.taskIdsByTag

## AppDatabase
- **version = 6**
- **fallbackToDestructiveMigration()** - عادي في مرحلة development
- entities: NoteEntity, TaskEntity, SessionEntity, AiChatEntity, TagEntity, NoteTagCrossRef, TaskTagCrossRef, GoalTagCrossRef, GoalEntity

## Graph System - مكتمل جزئيا

### الملفات الموجودة
- features/graph/domain/GraphNode.kt - NodeType, NodeIntent, EdgeType, GraphMode, GraphNode, Edge, GraphState
- features/graph/domain/GraphEngine.kt - Verlet Physics + Forces
- features/graph/presentation/GraphViewModel.kt - State + Data من Room
- features/graph/presentation/RenderModels.kt - RenderNode, RenderEdge, GraphSettings
- features/graph/presentation/GraphRenderer.kt - Canvas drawing functions
- features/graph/presentation/GraphScreen.kt - الشاشة الكاملة

### HomeScreen - زر الخريطة
- زر ShowChart في TopBar بيفتح route "graph"
- onNavigateToGraph مضاف للـ HomeScreen و AppNavigation
- onNavigateToStats لسه موجود للـ Drawer

### اللي ناقص في Graph (بالترتيب)
- المرحلة 14C UI - ايقونات جوا الـ nodes + labels كـ chips + glow قوي
- المرحلة 15 - Focus Mode + Radar Pulse
- المرحلة 16 - Timeline Mode
- المرحلة 17 - Smart Clustering
- المرحلة 18 - Discovery Mode
- المرحلة 19 - Energy System
- المرحلة 20 - Onboarding

### قواعد Graph مهمة
- GraphEngine = صفر UI
- GraphRenderer = صفر Physics
- الخلط = crash مضمون
- كل Composable اكبر من 150 سطر يتقسم

## اخطاء حدثت وتم حلها (الجلسة الحالية)
- MarkdownVisualTransformation كانت بتحذف الـ prefixes زي "# " و"## " من النص فبتنتج AnnotatedString اقصر من الاصلي - تم حل بكتابة buildMarkdownAnnotated من الصفر بـ append(text) كامل ثم addStyle فقط
- OffsetMapping.Identity كان بيعمل crash لان transformed text اقصر من original - السبب كان في buildMarkdownAnnotated مش في OffsetMapping
- ObsidianToolbar كان فيه savedValue و LaunchedEffect بيسببوا stale state - تم حذفهم واستخدام value مباشرة
- NoteDetailObsidianToolbar و NoteDetailBottomToolbar كانوا كود ميت - تم حذفهم
- parseInlineMarkdown كانت مكررة مع MarkdownEngine - تم حذفها واستبدالها بـ buildMarkdownAnnotated
- IntroScreen و OnboardingScreen كانوا معزولين عن AppNavigation - تم حذفهم
- sceneview dependency كان 28 ميجا بلا استخدام - تم حذفه (حجم APK من 44MB الى 16MB)
- fantasy_tree.glb كان في assets بلا استخدام - تم حذفه
- NoteDetailDeleteDialog اتحذف بالغلط مع الكود الميت - تم استعادته
- Regex في MarkdownEngine كانت بـ double quotes فبتعمل Illegal escape - تم تحويلها لـ triple quotes
- SharedObsidianToolbar كان جوا AnimatedVisibility مرتبط بـ imeVisible - لما تضغط زرار الكيبورد بيختفي فالتغيير مش بيحصل - تم فصله وربطه بـ isEditMode فقط
- MarkdownEngine.kt.save كان في مسار الكود - تم حذفه
- @Composable يتيم اتحذف بعد cleanup - تم حذفه
- smart quotes في NoteDetailDeleteDialog بدل double quotes عادية - تم تصحيحها

## المرحلة الجاية - Hybrid Editor
- القرار: تحويل محرر النصوص لـ WebView + CodeMirror 6
- المرحلة A: WebView + CodeMirror 6 في assets (offline كامل)
- المرحلة B: JavaScript Bridge للـ Toolbar (Kotlin يبعت اوامر لـ JavaScript)
- المرحلة C: Live Preview حقيقي (bold italic headings بدون علامات)
- المرحلة D: Graph View بـ D3.js بدل Canvas

## قواعد مهمة اتعلمناها
- buildMarkdownAnnotated لازم تعمل append(text) الاول وبعدين addStyle فقط - ما تحذفش اي حرف
- Regex في Kotlin لازم triple quotes مش double quotes
- ObsidianToolbar لازم يكون مرئي دايما في edit mode مش مرتبط بـ imeVisible
- لما تحذف كود بالـ line index ابدأ من الاخر للاول عشان الـ index ما يتغيرش
- smart quotes بتيجي لما تكتب نص عربي في Python heredoc - استخدم escaped quotes

## الناقص (الاولوية بالترتيب)
1. تحسين شكل GraphScreen (ايقونات + chips + glow)
2. Footnote في MarkdownEngine
3. Alias Link في MarkdownEngine
4. SearchScreen حقيقي
5. الضوضاء البيضاء - ملفات mp3
6. AI Integration بـ Groq
7. Export PDF
8. مسح crash logger من NoteFlowApp

## قواعد العمل من Termux
- الملفات بتتكتب بـ python3 heredoc دايما
- لا تستخدم cat > file << EOF - بيعمل مشاكل
- التعديلات الصغيرة - python3 heredoc
- nano 9.0 مثبت لكن خطر على ملفات كبيرة
- كل ملف 100-150 سطر max
- لا تستخدم printf مع نصوص عربية
- لا تستخدم emoji في python heredoc - بيعمل UnicodeEncodeError
- grep مع ! بيعمل bash history error
- الرفع دايما: git add -A && git commit -m "..." && git push
- GitHub Actions هو اللي يبني - مش Termux
- لو فشل البناء: اقرا السطر الاول من الخطا بس

## طريقة التعامل مع Claude
- في اول كل محادثة جديدة: ابعت محتوى CONTEXT.md
- بعدين قول: "اقرا CONTEXT.md وابدأ من الخطوة الجاية"
- قبل اي كود: Claude لازم يشوف الملف الحالي الاول
- Claude مش بيبدأ اي تعديل الا بموافقتك
- لو الملف كبير: بيتقسم لملفات صغيرة من الاول

## الخطة المعتمدة
- الخطة المعتمدة هي NoteFlow_Guide_v2
- مسار ملفات الـ Graph:
  features/graph/domain/GraphNode.kt
  features/graph/domain/GraphEngine.kt
  features/graph/presentation/GraphViewModel.kt
  features/graph/presentation/RenderModels.kt
  features/graph/presentation/GraphRenderer.kt
  features/graph/presentation/GraphScreen.kt
- مسار ملفات الـ Obsidian:
  ui/components/ObsidianToolbar.kt
  ui/components/ObsidianTextEngine.kt
  ui/components/MarkdownEngine.kt

## خطة Graph View النهائية - المراحل 14C-20

### Canvas Features لكل Node
- لون حسب NodeType
- ايقونة جوا الدايرة حسب النوع
- حجم = عدد الروابط
- Glow = طاقة + اهمية
- label تحت كل node بخلفية داكنة كـ chip
- اضغط = يفتح Preview
- اضغط مطول = Focus Mode

### Edge Visual
- opacity = strength
- سمك = strength
- متقطع = EdgeType.SIMILAR
- لون = EdgeType

### Bottom Bar في GraphScreen
- 4 ازرار: Graph mode, Edit, Search, Settings
- Filter bar في الاعلى
- Zoom controls على اليمين

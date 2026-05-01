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

## اعدادات البناء (مهم جدا لا تغير)
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

## اعدادات الـ Theme
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
- المرحلة A  - WebView + CodeMirror 6 offline
- المرحلة B  - Toolbar JavaScript Bridge
- المرحلة C  - Live Preview اساسي (bold italic headings checkboxes bullets)
- المرحلة C2 - Live Preview كامل (24 عنصر Markdown)

## Hybrid Editor System - مكتمل

### الملفات
- app/src/main/assets/editor.html
- app/src/main/assets/cm6.bundle.js (1.1MB - CodeMirror 6 offline)
- features/notes/presentation/screens/NoteEditorWebView.kt

### Architecture
- NoteEditorWebView - Composable بيعرض WebView
- NoteFlowBridge - JavascriptInterface بيستقبل التغييرات
- executeCommand - function بتبعت JavaScript commands للـ WebView
- ObsidianToolbar - onCommand parameter جديد بيبعت commands

### Live Preview - المرحلة C2 مكتملة
كل العناصر دي بتتrender بعيد عن الكيرسور وبترجع raw لما الكيرسور جوه:
1. Bold **text** - عريض بدون نجوم
2. Italic *text* - مائل
3. Bold+Italic ***text*** - عريض ومائل
4. Strikethrough ~~text~~ - مشطوب
5. Highlight ==text== - خلفية صفراء
6. Inline Code `code` - monospace ملون
7. Code Block ```lang``` - بلوك منسق + lp-codeblock class
8. Headings # H1 الى ###### H6 - تكبير + اخفاء #
9. Blockquote > text - border يمين + italic
10. Bullet List - item - نقطة ملونة
11. Numbered List 1. item - رقم ملون
12. Task List - [ ] و - [x] - checkbox حقيقي قابل للضغط
13. Horizontal Rule --- او *** او ___ - خط متدرج
14. External Link [text](url) - نص ملون بدون اقواس
15. Wiki Link [[note]] - بنفسجي بدون اقواس
16. Alias Link [[note|اسم]] - الاسم البديل فقط
17. Embed ![[note]] - ايقونة + اسم
18. Image ![[img.png]] او 

![alt](url)

 - ايقونة + اسم
19. Table | a | b | - صفوف ملونة + اخفاء separator
20. Footnote Ref [^1] - superscript رقم
21. Footnote Def [^1]: text - نص رمادي صغير
22. Escape \* - اخفاء backslash
23. HTML <b>text</b> - render النص بدون tags
24. Tags #tag - chip ملون
25. Comment %%text%% - مختفي كامل

### قاعدة الكيرسور
- cursorInside(state, from, to) - لو الكيرسور جوه الـ range يرجع raw markdown
- sel.from <= to && sel.to >= from - الشرط الصحيح

### قواعد index.js
- cm6-build/ في home directory مش جوا المشروع
- لو عايز تعدل: عدل ~/cm6-build/index.js ثم esbuild ثم التغيير بيتنعكس تلقائيا
- الكتابة دايما بـ python3 heredoc - جزء 1 (write) ثم جزء 2 (append)
- Build command: cd ~/cm6-build && npx esbuild index.js --bundle --minify --outfile=~/noteflow-android/app/src/main/assets/cm6.bundle.js

### Commands المتاحة في executeCommand
- undo, redo
- bold, italic, strikethrough, highlight, inlineCode
- h1, h2, h3, h4, h5, h6
- bullet, numbered, checkbox
- quote, codeblock, table, hr
- wikilink, embed, link, tag
- indent, unindent, callout

## Obsidian Toolbar System - مكتمل

### المسارات الصحيحة للملفات
- ui/components/ObsidianTextEngine.kt
- ui/components/ObsidianToolbar.kt
- ui/components/MarkdownEngine.kt

### ازرار الـ Toolbar الحالية
- Undo / Redo
- H1 H6 Dropdown (زرار واحد يفتح قائمة)
- Bold, Italic, Strikethrough, Highlight, Inline Code
- Bullet List, Numbered List, Checkbox
- Blockquote, Code Block, Table, Horizontal Rule
- Callout, Wiki Link, Embed, External Link, Tag
- Indent / Unindent

### MarkdownEngine - Rendering مكتمل (Kotlin - للـ QuickWrite فقط)
- H1 H6 باحجام مختلفة
- Bold, Italic, Strikethrough, Highlight
- Inline Code, Code Block
- Blockquote, Horizontal Rule, Checkboxes
- Bullet List, Numbered List
- Callouts بالوان: INFO WARNING TIP DANGER QUESTION NOTE
- Wiki Link, Embed, External Link, Tags
- Hidden Comment

## Goals System - مكتمل
- Goal.kt, GoalEntity.kt, GoalDao.kt
- GoalRepository interface + GoalRepositoryImpl
- AppDatabase version=6 + migration goals table
- AppModule - GoalDao + GoalRepository
- GoalViewModel, GoalsScreen.kt, GoalsComponents.kt
- GoalAddEditDialog.kt, GoalsUtils.kt
- AppNavigation - route "goals" مضاف

## Tags System - مكتمل
- Tag.kt, TagRepository.kt, TagExtractor.kt
- TagEntity, NoteTagCrossRef, TaskTagCrossRef, GoalTagCrossRef
- TagDao, AppDatabase migration 4->5
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
- features/graph/domain/GraphNode.kt
- features/graph/domain/GraphEngine.kt - Verlet Physics + Forces
- features/graph/presentation/GraphViewModel.kt
- features/graph/presentation/RenderModels.kt
- features/graph/presentation/GraphRenderer.kt
- features/graph/presentation/GraphScreen.kt

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

## اخطاء حدثت وتم حلها
- MarkdownVisualTransformation كانت بتحذف الـ prefixes - تم حل بـ buildMarkdownAnnotated
- OffsetMapping.Identity كان بيعمل crash - السبب في buildMarkdownAnnotated مش OffsetMapping
- ObsidianToolbar savedValue و LaunchedEffect بيسببوا stale state - تم حذفهم
- NoteDetailObsidianToolbar و NoteDetailBottomToolbar كود ميت - تم حذفهم
- sceneview dependency 28 ميجا بلا استخدام - تم حذفه (APK من 44MB الى 16MB)
- NoteDetailDeleteDialog اتحذف بالغلط - تم استعادته
- Regex في MarkdownEngine بـ double quotes - تم تحويلها لـ triple quotes
- SharedObsidianToolbar مرتبط بـ imeVisible - تم فصله وربطه بـ isEditMode
- decorations overlap في livePreviewPlugin - تم حل بـ lastTo tracking
- cursorInside كان sel.from >= from && sel.to <= to (خطا) - تم تصحيحه لـ sel.from <= to && sel.to >= from

## الناقص (الاولوية بالترتيب)
1. المرحلة D - Graph View بـ D3.js بدل Canvas
2. تحسين شكل GraphScreen (ايقونات + chips + glow) - المرحلة 14C UI
3. SearchScreen حقيقي
4. الضوضاء البيضاء - ملفات mp3
5. AI Integration بـ Groq
6. Export PDF
7. مسح crash logger من NoteFlowApp

## قواعد العمل من Termux
- الملفات بتتكتب بـ python3 heredoc دايما
- لا تستخدم cat > file << EOF - بيعمل مشاكل
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
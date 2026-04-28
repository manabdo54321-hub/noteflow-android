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
✅ المرحلة 12 — Obsidian Toolbar المرحلة 4 (Callouts + Advanced)

## Obsidian Toolbar System — مكتمل ✅

### المسارات الصحيحة للملفات
- ui/components/ObsidianTextEngine.kt
- ui/components/ObsidianToolbar.kt
- ui/components/MarkdownEngine.kt

### أزرار الـ Toolbar الحالية ✅
- Undo / Redo
- H1→H6 Dropdown (زرار واحد يفتح قائمة)
- Bold → **text**
- Italic → *text*
- Strikethrough → ~~text~~
- Highlight → ==text==
- Inline Code → `text`
- Bullet List → -
- Numbered List → 1.
- Checkbox → - [ ]
- Blockquote → >
- Code Block → ```
- Table → | col |
- Horizontal Rule → ---
- Callout → > [!INFO]
- Wiki Link → [[text]]
- Embed → ![[]]
- External Link → []()
- Tag → #
- Indent / Unindent

### MarkdownEngine — Rendering مكتمل ✅
- H1→H6 بأحجام مختلفة
- Bold, Italic, Strikethrough, Highlight
- Inline Code, Code Block
- Blockquote عادي
- Horizontal Rule ─────
- Checkboxes ✅ / ☐
- Bullet List, Numbered List
- Callouts بألوان: INFO(أزرق) WARNING(برتقالي) TIP(أخضر) DANGER(أحمر) QUESTION(بنفسجي) NOTE(أزرق)
- Wiki Link [[]] بلون بنفسجي + underline
- Embed ![[]] بلون تركواز + underline
- External Link [نص](رابط) بلون أزرق + underline
- Tags #tag بلون بنفسجي
- Hidden Comment %% — بيختفي في الـ rendering

### الناقص في Obsidian (مؤجل — مش أولوية)
- Footnote [^1]
- Alias Link [[ملف|اسم]]
- Block Link [[ملف#^id]]
- LaTeX $E=mc^2$ — محتاج WebView
- Mermaid Charts — محتاج WebView
- Canvas — محتاج library ضخمة
- Properties/YAML — مش أولوية
- H4/H5/H6 أزرار منفصلة (موجودة في Dropdown)
- Callouts إضافية: SUCCESS, BUG, EXAMPLE, QUOTE

### أخطاء حدثت وتم حلها
- MarkdownEngine.kt اتمسح بسبب nano — تم إعادة كتابته بـ cat
- UnicodeEncodeError في Python بسبب emoji — تم استبداله بنص عادي
- grep bash error مع ![[  — بسبب bash history expansion

## Goals System — مكتمل ✅
- Goal.kt — domain model
- GoalEntity.kt — Room entity
- GoalDao.kt — CRUD + Flow
- GoalRepository interface + GoalRepositoryImpl
- AppDatabase version=6 + migration goals table
- AppModule — GoalDao + GoalRepository
- GoalViewModel
- GoalsScreen.kt
- GoalsComponents.kt
- GoalAddEditDialog.kt
- GoalsUtils.kt
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
1. 🟠 NoteDetailScreen ← tag suggestions + highlighting
2. 🟡 Footnote [^1] في MarkdownEngine
3. 🟡 Alias Link [[ملف|اسم]] في MarkdownEngine
4. 🟡 SearchScreen حقيقي
5. 🟡 الضوضاء البيضاء — ملفات mp3
6. 🟢 AI Integration بـ Groq
7. 🟢 Graph View
8. 🟢 Export PDF
9. 🔵 مسح crash logger من NoteFlowApp

## آخر حاجة وصلنا ليها
- المرحلة 12 خلصت ✅
- Obsidian Toolbar كامل مع Callouts + External Link + Embed + Hidden Comment
- Build نجح ✅
- الخطوة الجاية: NoteDetailScreen ← tag suggestions + highlighting

## قواعد العمل من Termux
- الملفات بتتكتب بـ cat > file << 'EOF'
- التعديلات الصغيرة → python3 heredoc
- nano 9.0 مثبت ✅ لكن خطر على ملفات كبيرة (ممكن يمسح المحتوى)
- كل ملف 100-150 سطر max
- لا تستخدم printf مع نصوص عربية
- لا تستخدم emoji في python heredoc — بيعمل UnicodeEncodeError
- grep مع ! بيعمل bash history error — استخدم grep -n "%%" بدل grep -n "!\[\["
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
- مسار ملفات الـ Obsidian:
  ui/components/ObsidianToolbar.kt
  ui/components/ObsidianTextEngine.kt
  ui/components/MarkdownEngine.kt

## خطة Graph View النهائية — المراحل 13-20

### هيكل الملفات
- ui/screens/graph/GraphScreen.kt
- ui/screens/graph/GraphEngine.kt
- ui/screens/graph/GraphNode.kt
- ui/screens/graph/GraphRenderer.kt
- ui/screens/graph/GraphClustering.kt
- ui/screens/graph/GraphViewModel.kt

### Data Models
- NodeType: NOTE, TASK, GOAL, TAG
- NodeIntent: IDEA, ACTION, PLAN, KNOWLEDGE
- EdgeType: WIKI_LINK, TAG_SHARED, SIMILAR, GOAL_TASK
- Edge: from, to, strength(Float), type
- GraphMode: NORMAL, FOCUS, TIMELINE, DISCOVERY
- GraphState: zoom, offsetX, offsetY, focusedNodeId, currentMode

### قواعد Physics
- Verlet Integration (مش Euler)
- Force = Vector (x,y) مش قيمة واحدة
- Damping = 0.85f
- Max Speed = 10f
- Repulsion بين كل الـ Nodes
- Attraction = Edge.strength

### المراحل التفصيلية
- المرحلة 13  : NoteDetailScreen Tags
- المرحلة 14A : Graph Data Layer (Node + Edge + Intent)
- المرحلة 14B : Physics Engine (Verlet + Vector Forces)
- المرحلة 14C : Canvas + Interaction (Drag + Zoom + Memory)
- المرحلة 15  : Focus Mode + Radar Pulse
- المرحلة 16  : Timeline Mode (X=تاريخ Y=نوع)
- المرحلة 17  : Smart Clustering (Tag+Date+Words)
- المرحلة 18  : Discovery Mode (روابط مخفية + Pulse)
- المرحلة 19  : Energy System (حسب آخر فتح)
- المرحلة 20  : Onboarding بصري (Demo اول مرة)

### ترتيب التنفيذ الصح
1. 14A ثم 14B ثم 14C ثم اختبار
2. 15 ثم 16 ثم اختبار
3. 17 ثم 18 ثم 19 ثم 20

### Canvas Features لكل Node
- لون حسب NodeType
- حجم = عدد الروابط
- Glow = طاقة + اهمية
- اضغط = يفتح المحتوى
- اضغط مطول = Focus Mode
- سحب = Radar Pulse للمتشابهين

### Edge Visual
- opacity = strength
- سمك = strength
- متقطع = EdgeType.SIMILAR
- لون = EdgeType

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
- **jvmTarget:** 11 ← لا ترجعه لـ 17 أبداً
- **sourceCompatibility:** JavaVersion.VERSION_11
- **targetCompatibility:** JavaVersion.VERSION_11
- **Compose BOM:** 2023.08.00 ← لا ترفعه لـ 2024+ على Android 11
- **Kotlin Compiler Extension:** 1.5.8
- **AppCompat:** 1.6.1

## Permissions في AndroidManifest
- WRITE_EXTERNAL_STORAGE
- VIBRATE
- USE_EXACT_ALARM
- ACCESS_NOTIFICATION_POLICY

## إعدادات الـ Theme
- **themes.xml:** parent="Theme.AppCompat.DayNight.NoActionBar"
- لا تستخدم parent="android:Theme.DeviceDefault" — crash على Android 11
- **windowSoftInputMode:** adjustResize (مهم للكيبورد في HomeScreen)

## قواعد Android 11 — لازم تتبعها دايماً
1. **VerifyError** — أي Composable function أكبر من ~150 سطر UI logic تعمل crash
   - الحل: قسّم لـ composables أصغر
   - الشاشات المقسّمة:
     - HomeScreen → HomeTopBar, HomeQuickWrite, HomeCardsRow, HomeTasksCard, HomeTimerCard, HomeBottomNav, HomeLeftDrawer, HomeRightDrawer, ObsidianToolbar, HomeWritingMiniBar, AddBottomSheet, TimerFullScreen, TasksFullScreen
     - NoteDetailScreen → NoteDetailTopBar, NoteDetailTitle, NoteDetailTags, NoteDetailContentField, NoteDetailBacklinksHeader, NoteDetailBacklinkItem, NoteDetailBottomToolbar, NoteDetailDeleteDialog, ReadModeContent
     - TaskListScreen → TaskListHeader, TaskListTitle, TaskListTabs, TaskSectionLabel, TaskAddEditDialog, TaskNotePickerDialog, TaskDeleteDialog, TaskCard
     - StatsScreen → StatsHeader, StatsTitle, StatsCompletionCard, StatsFocusCard, StatsNotesCard, StatsFocusDistributionCard, StatsOverviewCard, StatsVersionFooter
     - SettingsScreen → SettingsHeader, SettingsSectionLabel, SettingsProfileCard, SettingsThemeCard, SettingsFunctionalCard, SettingsSystemCard, SettingsVersionFooter, SettingsToggleRow, SettingsArrowRow, SettingsDivider
     - TimerScreen → TimerTopBar, TimerTaskSelector, TimerCircleDisplay, TimerMotivationText, TimerMainControls, TimerBottomToolbar, TimerToolBtn, TimerTimePickerDialog, TimeScrollPicker, TimerTaskPickerDialog, TimerConfirmDialog, TimerNoiseBottomSheet, TimerModeBottomSheet, StrictModeSheet, StrictModeItem

2. **Modifier.blur()** — مش موجود على Android 11 — لا تستخدمه أبداً
3. **CompositingStrategy.Offscreen** — API 31+ فقط — لا تستخدمه
4. **Compose BOM 2024+** — بيستخدم APIs مش موجودة في Android 11
5. **jvmTarget = "17"** — بيعمل VerifyError — استخدم "11" دايماً

## قواعد الكود العامة
- ملف واحد في كل مرة
- الألوان الرئيسية: BgColor=#131313, SurfaceColor=#1C1B1B, SurfaceHigh=#2A2A2A, PrimaryColor=#CABEFF, AccentColor=#8A70FF, TertiaryColor=#75D1FF
- استخدم SimpleDivider أو SettingsDivider (custom Box) مش Divider() ولا HorizontalDivider()
- لو في تعديل على ملف قديم، ابعت محتواه الأول
- commit بعد كل ملف شغال
- أي Composable function أكبر من 150 سطر — قسّمها فوراً
- استخدم TextFieldValue بدل String لأي TextField محتاج cursor control

## هيكل التطبيق الحالي

### Navigation
- مفيش Bottom Navigation Bar — اتمسح عشان الشكل يكون نظيف
- الـ TimerViewModel بيتشارك على مستوى AppNavigation عشان مايوقفش لما المستخدم يرجع
- كل الـ routes: home, notes, note/{noteId}, tasks, timer, stats, settings, search

### HomeScreen — الرئيسية
- **Zen Mode:** لما تبدأ الكتابة، المهام والتايمر بيتلاشوا
- **Obsidian Style Writing:** عنوان + فاصل + محتوى، RTL، cursor بـ PrimaryColor
- **ObsidianToolbar:** شريط فوق الكيبورد فيه H1, H2, H3, B, I, •, ❝, [[, <>, —, ☐, @ — بيضيف في مكان الـ cursor بـ TextFieldValue
- **HomeWritingMiniBar:** لما تكتب — إنهاء + عدد المهام + وقت التايمر
- **HomeBottomNav:** ✏️ + ➕ + ⚡ + 🔍 + ⚙️ — الزائد بيفتح AddBottomSheet
- **AddBottomSheet:** ملاحظة جديدة + مهمة جديدة + ابدأ جلسة تركيز + كل الملاحظات
- **كارت التايمر:** الضغط عليه يروح لـ TimerScreen
- **كارت المهام:** الضغط عليه يفتح TasksFullScreen
- **imePadding():** للتعامل مع الكيبورد

### TimerScreen — التايمر
- **TimerViewModel مشترك** على مستوى AppNavigation — مش بيوقف لما ترجع
- **اختيار المهمة:** من قائمة المهام النشطة
- **الدائرة:** نابضة أثناء التشغيل (breatheScale)، بتعرض ساعات:دقائق:ثواني لو الوقت أكبر من ساعة
- **اضغط على الوقت:** يفتح TimeScrollPicker لاختيار الساعات والدقائق بالسحب
- **زرار ابدأ التركيز:** أبيض كبير، يتحول لـ "إيقاف مؤقت" أثناء التشغيل
- **زرار إيقاف + تخطي:** ظاهرين أثناء التشغيل فقط مع Dialog تأكيد
- **جلسات:** 4 جلسات تركيز → استراحة كبيرة 15 دقيقة، بين كل جلسة استراحة 5 دقائق
- **جرس + اهتزاز:** لما ينتهي الوقت (RingtoneManager + Vibrator)
- **رسائل تحفيزية:** تتغير كل جلسة
- **تنفس موجّه:** في وقت الراحة (شهيق/زفير animation)
- **ضوضاء بيضاء:** Sheet بـ 7 خيارات (بدون صوت حقيقي لسه — ناقص ملفات)
- **وضع المؤقت:** تنازلي (افتراضي) أو تصاعدي
- **الوضع الصارم:**
  - اقلب الهاتف: Accelerometer — لو رفع موبايله تحذير + اهتزاز
  - حظر الإشعارات: DND — لو مش موجود permission يفتح الإعدادات تلقائياً
  - منع الخروج: BackHandler — Dialog تأكيد لو حاول يخرج
  - قفل الهاتف + حظر التطبيقات: غير متاح (شو بس)

## الملفات الموجودة

### Base
- settings.gradle.kts / build.gradle.kts / gradle.properties / gradlew
- app/build.gradle.kts
- .github/workflows/build.yml
- app/src/main/AndroidManifest.xml
- app/src/main/res/values/themes.xml
- app/src/main/java/com/noteflow/app/MainActivity.kt
- app/src/main/java/com/noteflow/app/NoteFlowApp.kt (crash logger موجود — يتمسح قبل النشر)

### Core
- app/src/main/java/com/noteflow/app/core/database/AppDatabase.kt
- app/src/main/java/com/noteflow/app/core/di/AppModule.kt
- app/src/main/java/com/noteflow/app/core/navigation/AppNavigation.kt ← فيه TimerViewModel مشترك

### الرئيسية
- app/src/main/java/com/noteflow/app/features/home/presentation/HomeScreen.kt ✅ مقسّمة + Zen Mode + Obsidian

### الملاحظات
- app/src/main/java/com/noteflow/app/features/notes/domain/model/Note.kt
- app/src/main/java/com/noteflow/app/features/notes/data/local/NoteEntity.kt
- app/src/main/java/com/noteflow/app/features/notes/data/local/NoteDao.kt
- app/src/main/java/com/noteflow/app/features/notes/data/repository/NoteRepository.kt
- app/src/main/java/com/noteflow/app/features/notes/domain/usecase/GetNotesUseCase.kt
- app/src/main/java/com/noteflow/app/features/notes/domain/usecase/SaveNoteUseCase.kt
- app/src/main/java/com/noteflow/app/features/notes/presentation/NoteViewModel.kt
- app/src/main/java/com/noteflow/app/features/notes/presentation/screens/NoteListScreen.kt
- app/src/main/java/com/noteflow/app/features/notes/presentation/screens/NoteDetailScreen.kt ✅ مقسّمة

### المهام
- app/src/main/java/com/noteflow/app/features/tasks/domain/model/Task.kt
- app/src/main/java/com/noteflow/app/features/tasks/data/local/TaskEntity.kt
- app/src/main/java/com/noteflow/app/features/tasks/data/local/TaskDao.kt
- app/src/main/java/com/noteflow/app/features/tasks/data/repository/TaskRepository.kt
- app/src/main/java/com/noteflow/app/features/tasks/presentation/TaskViewModel.kt
- app/src/main/java/com/noteflow/app/features/tasks/presentation/screens/TaskListScreen.kt ✅ مقسّمة

### التايمر
- app/src/main/java/com/noteflow/app/features/timer/data/local/SessionEntity.kt
- app/src/main/java/com/noteflow/app/features/timer/data/local/SessionDao.kt
- app/src/main/java/com/noteflow/app/features/timer/data/repository/SessionRepository.kt
- app/src/main/java/com/noteflow/app/features/timer/presentation/TimerViewModel.kt ← مشترك في AppNavigation
- app/src/main/java/com/noteflow/app/features/timer/presentation/screens/TimerScreen.kt ✅ مقسّمة + كل الميزات

### الإحصائيات
- app/src/main/java/com/noteflow/app/features/stats/presentation/StatsViewModel.kt
- app/src/main/java/com/noteflow/app/features/stats/presentation/screens/StatsScreen.kt ✅ مقسّمة

### الإعدادات
- app/src/main/java/com/noteflow/app/features/settings/presentation/screens/SettingsScreen.kt ✅ مقسّمة

### البحث
- app/src/main/java/com/noteflow/app/features/search/presentation/SearchScreen.kt ← ناقص بحث حقيقي

## المراحل المكتملة
✅ المرحلة 0 — Build شغال
✅ المرحلة 1 — قاعدة البيانات
✅ المرحلة 2 — شاشات الملاحظات
✅ المرحلة 3 — المهام والبومودورو
✅ المرحلة 4 — الربط والذكاء
✅ المرحلة 5 — إصلاح الـ crash على Android 11
✅ المرحلة 5.5 — تقسيم كل الشاشات
✅ المرحلة 6 — HomeScreen (Zen Mode + Obsidian + Bottom Sheet)
✅ المرحلة 7 — TimerScreen كامل (جرس + وضع صارم + اختيار وقت + نبض)

## الناقص (الأولوية بالترتيب)
1. 🔴 SearchScreen حقيقي — بحث في الملاحظات والمهام
2. 🟠 NoteDetailScreen — Obsidian toolbar زي HomeScreen
3. 🟡 الضوضاء البيضاء — ملفات صوت mp3 في res/raw (ناقص الملفات)
4. 🟢 AI Integration بـ Groq
5. 🟢 Graph View للملاحظات
6. 🟢 Export PDF
7. 🔵 مسح crash logger من NoteFlowApp قبل النشر

## الخطوة الجاية
المرحلة 8: SearchScreen حقيقي
- بحث في عنوان وmحتوى الملاحظات
- بحث في المهام
- نتائج فورية أثناء الكتابة
- فلترة بالنوع (ملاحظات/مهام)

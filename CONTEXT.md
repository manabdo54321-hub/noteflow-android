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

## إعدادات البناء الحالية (مهم جداً)
- **minSdk:** 26
- **compileSdk:** 34
- **targetSdk:** 34
- **jvmTarget:** 11 ← مهم، لا ترجعه لـ 17
- **sourceCompatibility:** JavaVersion.VERSION_11 ← مهم
- **targetCompatibility:** JavaVersion.VERSION_11 ← مهم
- **Compose BOM:** 2023.08.00 ← لا ترفعه لـ 2024+ على Android 11
- **Kotlin Compiler Extension:** 1.5.8
- **AppCompat:** 1.6.1 ← مطلوب للـ theme

## إعدادات الـ Theme (مهم جداً)
- **themes.xml:** parent="Theme.AppCompat.DayNight.NoActionBar"
- لا تستخدم parent="android:Theme.DeviceDefault" — بيعمل crash على Android 11

## قواعد Android 11 — لازم تتبعها دايماً
1. **VerifyError** — لو HomeScreen أو أي Composable function كبيرة جداً (أكتر من ~200 سطر من الـ UI logic في function واحدة) هتعمل crash على Android 11 بسبب تجاوز حد الـ registers في الـ bytecode
   - **الحل:** قسّم كل Composable كبيرة لـ functions أصغر (زي ما عملنا في HomeScreen)
   - **HomeScreen اتقسمت لـ:** HomeTopBar, HomeQuickWrite, HomeCardsRow, HomeTasksCard, HomeTimerCard, HomeBottomNav, HomeLeftDrawer, HomeRightDrawer

2. **Modifier.blur()** — مش موجود على Android 11 (API 30)، بيعمل crash فوري
   - **الحل:** لا تستخدمه خالص، استخدم alpha أو background بدل منه

3. **CompositingStrategy.Offscreen** — API 31+ فقط، مش شغال على Android 11
   - **الحل:** لا تستخدمه

4. **Compose BOM 2024+** — بيستخدم داخلياً APIs مش موجودة في Android 11
   - **الحل:** ابقى على BOM 2023.08.00

5. **jvmTarget = "17"** — بيعمل VerifyError على Android 11
   - **الحل:** استخدم jvmTarget = "11" دايماً

## قواعد الكود العامة
- ملف واحد في كل مرة
- الألوان: BgColor=#131313, SurfaceColor=#1C1B1B, PrimaryColor=#CABEFF, AccentColor=#8A70FF
- استخدم SimpleDivider (custom Box) مش Divider() ولا HorizontalDivider()
- لو في تعديل على ملف قديم، ابعت محتواه الأول
- commit بعد كل ملف شغال
- أي Composable function أكبر من 150 سطر — قسّمها لـ functions أصغر

## هيكل التطبيق
- **5 Tabs:** الرئيسية / ملاحظات / إضافة / مهام / إحصائيات
- **Architecture:** Clean Architecture (Domain / Data / Presentation)
- **Versioning:** تلقائي من عدد الـ commits في build.gradle.kts
- **Theme:** داكن #131313 مع بنفسجي #CABEFF

## الملفات الموجودة

### Base
- settings.gradle.kts
- build.gradle.kts
- gradle/wrapper/gradle-wrapper.properties
- gradle/wrapper/gradle-wrapper.jar
- gradle.properties
- gradlew
- app/build.gradle.kts
- .github/workflows/build.yml
- app/src/main/AndroidManifest.xml
- app/src/main/res/values/themes.xml
- app/src/main/res/drawable/ic_splash.xml
- app/src/main/java/com/noteflow/app/MainActivity.kt
- app/src/main/java/com/noteflow/app/NoteFlowApp.kt

### Core
- app/src/main/java/com/noteflow/app/core/database/AppDatabase.kt
- app/src/main/java/com/noteflow/app/core/di/AppModule.kt
- app/src/main/java/com/noteflow/app/core/navigation/AppNavigation.kt

### Intro & Onboarding
- app/src/main/java/com/noteflow/app/features/intro/presentation/IntroScreen.kt
- app/src/main/java/com/noteflow/app/features/intro/presentation/OnboardingScreen.kt

### الرئيسية
- app/src/main/java/com/noteflow/app/features/home/presentation/HomeScreen.kt

### الملاحظات
- app/src/main/java/com/noteflow/app/features/notes/domain/model/Note.kt
- app/src/main/java/com/noteflow/app/features/notes/data/local/NoteEntity.kt
- app/src/main/java/com/noteflow/app/features/notes/data/local/NoteDao.kt
- app/src/main/java/com/noteflow/app/features/notes/data/repository/NoteRepository.kt
- app/src/main/java/com/noteflow/app/features/notes/domain/usecase/GetNotesUseCase.kt
- app/src/main/java/com/noteflow/app/features/notes/domain/usecase/SaveNoteUseCase.kt
- app/src/main/java/com/noteflow/app/features/notes/presentation/NoteViewModel.kt
- app/src/main/java/com/noteflow/app/features/notes/presentation/screens/NoteListScreen.kt
- app/src/main/java/com/noteflow/app/features/notes/presentation/screens/NoteDetailScreen.kt

### المهام
- app/src/main/java/com/noteflow/app/features/tasks/domain/model/Task.kt
- app/src/main/java/com/noteflow/app/features/tasks/data/local/TaskEntity.kt
- app/src/main/java/com/noteflow/app/features/tasks/data/local/TaskDao.kt
- app/src/main/java/com/noteflow/app/features/tasks/data/repository/TaskRepository.kt
- app/src/main/java/com/noteflow/app/features/tasks/presentation/TaskViewModel.kt
- app/src/main/java/com/noteflow/app/features/tasks/presentation/screens/TaskListScreen.kt

### التايمر
- app/src/main/java/com/noteflow/app/features/timer/data/local/SessionEntity.kt
- app/src/main/java/com/noteflow/app/features/timer/data/local/SessionDao.kt
- app/src/main/java/com/noteflow/app/features/timer/data/repository/SessionRepository.kt
- app/src/main/java/com/noteflow/app/features/timer/presentation/TimerViewModel.kt
- app/src/main/java/com/noteflow/app/features/timer/presentation/screens/TimerScreen.kt

### الإحصائيات
- app/src/main/java/com/noteflow/app/features/stats/presentation/StatsViewModel.kt
- app/src/main/java/com/noteflow/app/features/stats/presentation/screens/StatsScreen.kt

### الإعدادات
- app/src/main/java/com/noteflow/app/features/settings/presentation/screens/SettingsScreen.kt

### البحث
- app/src/main/java/com/noteflow/app/features/search/presentation/SearchScreen.kt

## المراحل المكتملة
✅ المرحلة 0 — Build شغال
✅ المرحلة 1 — قاعدة البيانات
✅ المرحلة 2 — شاشات الملاحظات
✅ المرحلة 3 — المهام والبومودورو
✅ المرحلة 4 — الربط والذكاء
✅ المرحلة 5 — إصلاح الـ crash على Android 11
✅ تحسينات مضافة:
   - Intro + Onboarding screens
   - HomeScreen مع Daily Mastery + streak + Daily Goal
   - NoteListScreen مع Search + Filters
   - NoteDetailScreen مع Markdown + Tags + Connections
   - TaskListScreen مع Priority groups + badges
   - TimerScreen مع Session counter + Task picker
   - StatsScreen مع Charts
   - SettingsScreen مع Theme picker
   - Backlinks + Auto-save + حذف وتعديل الملاحظات والمهام
   - SearchScreen

## الخطوة الجاية
المرحلة 6: الميزات المتقدمة
- Markdown Rendering كامل
- Graph View
- AI Integration بـ Groq
- Tags & Folders
- Export PDF

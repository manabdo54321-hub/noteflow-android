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

## المراحل المكتملة
✅ المرحلة 0 — Build شغال
✅ المرحلة 1 — قاعدة البيانات
✅ المرحلة 2 — شاشات الملاحظات
✅ المرحلة 3 — المهام والبومودورو
✅ المرحلة 4 — الربط والذكاء
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
   - شاشة Splash Screen + Onboarding أول مرة بس

## قواعد مهمة
- ملف واحد في كل مرة
- الألوان: BgColor=#131313, SurfaceColor=#1C1B1B, PrimaryColor=#CABEFF, AccentColor=#8A70FF
- استخدم Divider() مش HorizontalDivider
- لو في تعديل على ملف قديم، ابعت محتواه الأول
- commit بعد كل ملف شغال

## الخطوة الجاية
المرحلة 5: الميزات المتقدمة
- Markdown Rendering كامل
- Graph View
- AI Integration بـ Groq
- Tags & Folders
- Export PDF

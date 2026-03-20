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
- **5 Tabs:** ملاحظات / مهام / تايمر / إحصائيات / إعدادات
- **Navigation:** Bottom Navigation + شاشة تفاصيل الملاحظة
- **Architecture:** Clean Architecture (Domain / Data / Presentation)
- **Versioning:** تلقائي من عدد الـ commits في build.gradle.kts

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
- app/src/main/java/com/noteflow/app/MainActivity.kt
- app/src/main/java/com/noteflow/app/NoteFlowApp.kt

### Core
- app/src/main/java/com/noteflow/app/core/database/AppDatabase.kt
- app/src/main/java/com/noteflow/app/core/di/AppModule.kt
- app/src/main/java/com/noteflow/app/core/navigation/AppNavigation.kt

### الملاحظات (Notes)
- app/src/main/java/com/noteflow/app/features/notes/domain/model/Note.kt
- app/src/main/java/com/noteflow/app/features/notes/data/local/NoteEntity.kt
- app/src/main/java/com/noteflow/app/features/notes/data/local/NoteDao.kt
- app/src/main/java/com/noteflow/app/features/notes/data/repository/NoteRepository.kt
- app/src/main/java/com/noteflow/app/features/notes/domain/usecase/GetNotesUseCase.kt
- app/src/main/java/com/noteflow/app/features/notes/domain/usecase/SaveNoteUseCase.kt
- app/src/main/java/com/noteflow/app/features/notes/presentation/NoteViewModel.kt
- app/src/main/java/com/noteflow/app/features/notes/presentation/screens/NoteListScreen.kt
- app/src/main/java/com/noteflow/app/features/notes/presentation/screens/NoteDetailScreen.kt

### المهام (Tasks)
- app/src/main/java/com/noteflow/app/features/tasks/domain/model/Task.kt
- app/src/main/java/com/noteflow/app/features/tasks/data/local/TaskEntity.kt
- app/src/main/java/com/noteflow/app/features/tasks/data/local/TaskDao.kt
- app/src/main/java/com/noteflow/app/features/tasks/data/repository/TaskRepository.kt
- app/src/main/java/com/noteflow/app/features/tasks/presentation/TaskViewModel.kt
- app/src/main/java/com/noteflow/app/features/tasks/presentation/screens/TaskListScreen.kt

### التايمر (Timer)
- app/src/main/java/com/noteflow/app/features/timer/data/local/SessionEntity.kt
- app/src/main/java/com/noteflow/app/features/timer/data/local/SessionDao.kt
- app/src/main/java/com/noteflow/app/features/timer/data/repository/SessionRepository.kt
- app/src/main/java/com/noteflow/app/features/timer/presentation/TimerViewModel.kt
- app/src/main/java/com/noteflow/app/features/timer/presentation/screens/TimerScreen.kt

### الإحصائيات (Stats)
- app/src/main/java/com/noteflow/app/features/stats/presentation/screens/StatsScreen.kt

### الإعدادات (Settings)
- app/src/main/java/com/noteflow/app/features/settings/presentation/screens/SettingsScreen.kt

## المراحل المكتملة
✅ المرحلة 0 — Build شغال
✅ المرحلة 1 — قاعدة البيانات جاهزة (Room + Hilt)
✅ المرحلة 2 — شاشات الملاحظات (قائمة + تفاصيل + بحث)
✅ المرحلة 3 — المهام والبومودورو (Tasks + Timer + Sessions)
✅ المرحلة 4 — الربط والذكاء:
   - Backlinks: [[اسم]] رابط قابل للضغط + قائمة الملاحظات اللي بتلينك عليك
   - ربط مهمة بملاحظة
   - ربط جلسة التايمر بمهمة
   - شاشة الإحصائيات
   - شاشة الإعدادات مع رقم الإصدار التلقائي

## قواعد مهمة
- ملف واحد في كل مرة
- الألوان تيجي من MaterialTheme مش hardcoded
- HorizontalDivider → استخدم Divider() بدلها
- لو في تعديل على ملف قديم، ابعت محتواه الأول
- AppDatabase.kt هو الملف الوحيد اللي بيتلمس من القديم لما نضيف feature جديدة
- commit بعد كل ملف شغال

## الخطوة الجاية
المرحلة 5: الميزات المتقدمة
أول حاجة: Markdown Rendering — **bold** و*italic* و# عنوان تتعرض صح

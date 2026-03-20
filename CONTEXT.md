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

## الملفات الموجودة
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
- app/src/main/java/com/noteflow/app/features/notes/domain/model/Note.kt
- app/src/main/java/com/noteflow/app/features/notes/data/local/NoteEntity.kt
- app/src/main/java/com/noteflow/app/features/notes/data/local/NoteDao.kt
- app/src/main/java/com/noteflow/app/core/database/AppDatabase.kt
- app/src/main/java/com/noteflow/app/features/notes/data/repository/NoteRepository.kt
- app/src/main/java/com/noteflow/app/core/di/AppModule.kt
- app/src/main/java/com/noteflow/app/features/notes/domain/usecase/GetNotesUseCase.kt
- app/src/main/java/com/noteflow/app/features/notes/domain/usecase/SaveNoteUseCase.kt
- app/src/main/java/com/noteflow/app/features/notes/presentation/NoteViewModel.kt
- app/src/main/java/com/noteflow/app/features/notes/presentation/screens/NoteListScreen.kt
- app/src/main/java/com/noteflow/app/features/notes/presentation/screens/NoteDetailScreen.kt
- app/src/main/java/com/noteflow/app/core/navigation/AppNavigation.kt
- app/src/main/java/com/noteflow/app/features/tasks/domain/model/Task.kt
- app/src/main/java/com/noteflow/app/features/tasks/data/local/TaskEntity.kt
- app/src/main/java/com/noteflow/app/features/tasks/data/local/TaskDao.kt
- app/src/main/java/com/noteflow/app/features/tasks/data/repository/TaskRepository.kt
- app/src/main/java/com/noteflow/app/features/tasks/presentation/TaskViewModel.kt
- app/src/main/java/com/noteflow/app/features/tasks/presentation/screens/TaskListScreen.kt
- app/src/main/java/com/noteflow/app/features/timer/data/local/SessionEntity.kt
- app/src/main/java/com/noteflow/app/features/timer/data/local/SessionDao.kt
- app/src/main/java/com/noteflow/app/features/timer/data/repository/SessionRepository.kt
- app/src/main/java/com/noteflow/app/features/timer/presentation/TimerViewModel.kt
- app/src/main/java/com/noteflow/app/features/timer/presentation/screens/TimerScreen.kt

## المرحلة الحالية
✅ المرحلة 0 مكتملة — Build شغال
✅ المرحلة 1 مكتملة — قاعدة البيانات جاهزة
✅ المرحلة 2 مكتملة — شاشات الملاحظات شغالة
✅ المرحلة 3 مكتملة — المهام والبومودورو شغالين

## الخطوة الجاية
المرحلة 4: الربط والذكاء
أول حاجة: Backlinks — ربط الملاحظات ببعض

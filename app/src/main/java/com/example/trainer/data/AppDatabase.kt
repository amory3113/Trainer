package com.example.trainer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
// Импортируем наши новые файлы
import com.example.trainer.data.Exercise.ExerciseEntity
import com.example.trainer.data.Exercise.WorkoutTemplateEntity
import com.example.trainer.data.Exercise.WorkoutExerciseEntity
import com.example.trainer.data.Exercise.ScheduleEntity
import com.example.trainer.data.Exercise.ExerciseDao
import com.example.trainer.data.Exercise.WorkoutDao

// 1. Добавляем новые классы в список entities
@Database(
    entities = [
        UserEntity::class,
        WeightEntity::class,
        NutritionEntity::class,
        // Новые:
        ExerciseEntity::class,
        WorkoutTemplateEntity::class,
        WorkoutExerciseEntity::class,
        ScheduleEntity::class
    ],
    version = 4, // <-- ВАЖНО: Увеличили версию!
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    // 2. Добавляем доступ к новым DAO
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trainer_database"
                )
                    .fallbackToDestructiveMigration() // Это удалит старую базу при обновлении версии. Для разработки ОК.
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
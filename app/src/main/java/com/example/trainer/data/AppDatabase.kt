package com.example.trainer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 1. Указываем, какие таблицы есть (UserEntity) и версию базы (1)
@Database(entities = [UserEntity::class, WeightEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // 2. Даем доступ к пульту управления (Dao)
    abstract fun userDao(): UserDao

    // 3. Стандартный код для создания Singleton (Копипаст для любого Room проекта)
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trainer_database" // Имя файла на телефоне
                )
                    // .fallbackToDestructiveMigration() // Раскомментируй, если будешь менять структуру таблицы и захочешь просто удалять старую
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
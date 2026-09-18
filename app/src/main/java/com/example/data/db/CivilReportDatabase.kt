package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CivilReportEntity::class], version = 1, exportSchema = false)
abstract class CivilReportDatabase : RoomDatabase() {
    abstract fun reportDao(): CivilReportDao

    companion object {
        @Volatile
        private var INSTANCE: CivilReportDatabase? = null

        fun getDatabase(context: Context): CivilReportDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CivilReportDatabase::class.java,
                    "civil_reports_db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

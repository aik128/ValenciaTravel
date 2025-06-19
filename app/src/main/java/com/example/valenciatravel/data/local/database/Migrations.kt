package com.example.valenciatravel.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `category_ratings` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `userId` INTEGER NOT NULL,
                `categoryId` INTEGER NOT NULL,
                `isLiked` INTEGER NOT NULL,
                FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """
        )
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_category_ratings_userId` ON `category_ratings` (`userId`)")
    }
}
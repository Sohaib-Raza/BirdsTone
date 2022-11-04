package com.sound.birdstone.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BirdEntity::class], version = 1, exportSchema = true)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun birdDao(): BirdDao
}
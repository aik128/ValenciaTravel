package com.example.valenciatravel.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.valenciatravel.data.local.entity.PreferencesEntity

@Dao
interface PreferencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(preferences: PreferencesEntity)

    @Query("SELECT * FROM preferences WHERE userId = :userId")
    suspend fun getPreferencesByUserId(userId: Long): PreferencesEntity?

    @Query("UPDATE preferences SET c1 = :value WHERE userId = :userId")
    suspend fun updateC1(userId: Long, value: Int)


    @Query("UPDATE preferences SET c2 = :value WHERE userId = :userId")
    suspend fun updateC2(userId: Long, value: Int)

    @Query("UPDATE preferences SET c3 = :value WHERE userId = :userId")
    suspend fun updateC3(userId: Long, value: Int)

    @Query("UPDATE preferences SET c4 = :value WHERE userId = :userId")
    suspend fun updateC4(userId: Long, value: Int)

    @Query("UPDATE preferences SET c5 = :value WHERE userId = :userId")
    suspend fun updateC5(userId: Long, value: Int)

    @Query("UPDATE preferences SET c6 = :value WHERE userId = :userId")
    suspend fun updateC6(userId: Long, value: Int)

    @Query("UPDATE preferences SET c7 = :value WHERE userId = :userId")
    suspend fun updateC7(userId: Long, value: Int)

    @Query("UPDATE preferences SET c8 = :value WHERE userId = :userId")
    suspend fun updateC8(userId: Long, value: Int)

    @Query("UPDATE preferences SET c9 = :value WHERE userId = :userId")
    suspend fun updateC9(userId: Long, value: Int)

    @Query("UPDATE preferences SET c10 = :value WHERE userId = :userId")
    suspend fun updateC10(userId: Long, value: Int)
}
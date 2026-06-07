package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SierDao {
    @Query("SELECT * FROM sier_table ORDER BY dateYear DESC, dateMonth DESC, dateDay DESC")
    fun getAllSiers(): Flow<List<Sier>>

    @Query("SELECT * FROM sier_table WHERE id = :id LIMIT 1")
    suspend fun getSierById(id: Int): Sier?

    @Query("SELECT * FROM sier_table WHERE dateYear = :year AND dateMonth = :month ORDER BY dateDay DESC")
    fun getSiersByMonth(year: Int, month: Int): Flow<List<Sier>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSier(sier: Sier): Long

    @Update
    suspend fun updateSier(sier: Sier)

    @Delete
    suspend fun deleteSier(sier: Sier)

    @Query("DELETE FROM sier_table WHERE id = :id")
    suspend fun deleteSierById(id: Int)
}

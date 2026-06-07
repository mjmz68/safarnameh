package com.example.data

import kotlinx.coroutines.flow.Flow

class SierRepository(private val sierDao: SierDao) {
    val allSiers: Flow<List<Sier>> = sierDao.getAllSiers()

    suspend fun getSierById(id: Int): Sier? = sierDao.getSierById(id)

    fun getSiersByMonth(year: Int, month: Int): Flow<List<Sier>> = sierDao.getSiersByMonth(year, month)

    suspend fun insert(sier: Sier): Long = sierDao.insertSier(sier)

    suspend fun update(sier: Sier) = sierDao.updateSier(sier)

    suspend fun delete(sier: Sier) = sierDao.deleteSier(sier)

    suspend fun deleteById(id: Int) = sierDao.deleteSierById(id)
}

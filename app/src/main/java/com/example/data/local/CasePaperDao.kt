package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CasePaper
import kotlinx.coroutines.flow.Flow

@Dao
interface CasePaperDao {
    @Query("SELECT * FROM case_papers ORDER BY id DESC")
    fun getAllCasePapers(): Flow<List<CasePaper>>

    @Query("SELECT * FROM case_papers WHERE patientId = :patientId ORDER BY id DESC")
    fun getCasePapersForPatient(patientId: String): Flow<List<CasePaper>>

    @Query("SELECT * FROM case_papers WHERE casePaperId = :casePaperId LIMIT 1")
    fun getCasePaperById(casePaperId: String): Flow<CasePaper?>

    @Query("SELECT COUNT(*) FROM case_papers")
    fun getCasePaperCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM case_papers")
    suspend fun getCasePaperCountDirect(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCasePaper(casePaper: CasePaper): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(casePapers: List<CasePaper>)

    @Update
    suspend fun updateCasePaper(casePaper: CasePaper)

    @Delete
    suspend fun deleteCasePaper(casePaper: CasePaper)

    @Query("DELETE FROM case_papers")
    suspend fun clearAll()
}

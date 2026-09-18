package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CivilReportDao {

    @Query("SELECT * FROM civil_reports ORDER BY createdTimestamp DESC")
    fun getAllReports(): Flow<List<CivilReportEntity>>

    @Query("SELECT * FROM civil_reports WHERE reportNumber = :reportNo LIMIT 1")
    suspend fun getReportByNumber(reportNo: String): CivilReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: CivilReportEntity)

    @Query("DELETE FROM civil_reports WHERE reportNumber = :reportNo")
    suspend fun deleteReport(reportNo: String)

    @Query("SELECT COUNT(*) FROM civil_reports")
    suspend fun getReportCount(): Int
}

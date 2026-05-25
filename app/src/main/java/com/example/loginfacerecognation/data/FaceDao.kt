package com.example.loginfacerecognation.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FaceDao {
    @Query("SELECT * FROM faces")
    suspend fun getAllFaces(): List<FaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFace(face: FaceEntity)

    @Query("DELETE FROM faces")
    suspend fun deleteAllFaces()
}

package com.example.yourapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface ProfileDao {
    @Insert
    suspend fun insertProfile(profile: Profile)

    @Query("SELECT * FROM profiles")
    suspend fun getAllProfiles(): List<Profile>

    @Delete
    suspend fun deleteProfile(profile: Profile)
}

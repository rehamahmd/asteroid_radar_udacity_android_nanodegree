package com.udacity.asteroidradar.database


import android.graphics.Picture
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay

@Dao
interface DailyPictureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(picture: PictureOfDay): Long


    @Query("SELECT * from picture_table WHERE url=:url")
    fun getPictureOfDay(url: String): PictureOfDay


    @Query("SELECT * from picture_table LIMIT 1")
    fun getAllPictures(): List<PictureOfDay>
}
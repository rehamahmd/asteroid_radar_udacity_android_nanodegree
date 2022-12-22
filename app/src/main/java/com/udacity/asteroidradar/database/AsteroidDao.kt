package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData

import androidx.room.*
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(asteroids: List<Asteroid>): List<Long>

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate = :startDate ORDER BY closeApproachDate DESC")
    fun getTodayAsteroid(startDate: String):  List<Asteroid>

    @Query("SELECT * FROM asteroid_table ORDER BY closeApproachDate DESC")
    fun getAllAsteroids(): List<Asteroid>


    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate BETWEEN :startDate AND :endDate ORDER BY closeApproachDate DESC")
    fun getAsteroidsDate(startDate: String, endDate: String):  List<Asteroid>



}

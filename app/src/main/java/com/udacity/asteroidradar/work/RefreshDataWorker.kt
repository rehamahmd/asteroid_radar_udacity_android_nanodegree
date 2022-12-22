package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.load.HttpException
import com.google.gson.JsonParser
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DailyPictureDao
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class RefreshDataWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshAsteroidsWorker"
    }
    private val asteroidDao: AsteroidDao by lazy {
        AsteroidDatabase.getInstance(applicationContext).asteroidDao
    }
    private val pictureDao: DailyPictureDao by lazy {
        AsteroidDatabase.getInstance(applicationContext).pictureOfDayDao
    }


    // Create a job to fetch all asteroids and save them to DB
    override suspend fun doWork(): Result {
        return try  {
            val calendar = Calendar.getInstance()
            val currentTime = calendar.time

            calendar.add(Calendar.DAY_OF_YEAR, 7)
            val futureTime = calendar.time
            val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

            val response = AsteroidApi.retrofitService.getAsteroids(
                dateFormat.format(currentTime),
                dateFormat.format(futureTime)
            )
            val gson = JsonParser().parse(response.toString()).asJsonObject
            val astList = parseAsteroidsJsonResult(JSONObject(gson.toString()))
            asteroidDao.insert(astList)

            // Get Image
            val picture = AsteroidApi.retrofitService.getPictureOfDay()
            pictureDao.insert(picture)


            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

}
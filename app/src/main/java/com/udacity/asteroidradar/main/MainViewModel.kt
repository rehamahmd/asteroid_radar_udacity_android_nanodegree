package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.gson.JsonParser
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.DailyPictureDao

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _picture: MutableLiveData<PictureOfDay?> = MutableLiveData<PictureOfDay?>(null);
    val picture: LiveData<PictureOfDay?> = _picture

    private val _asteroidsList: MutableLiveData<List<Asteroid>> = MutableLiveData<List<Asteroid>>();
    val asteroidsList: LiveData<List<Asteroid>> = _asteroidsList

    private val asteroidDao: AsteroidDao by lazy {
        AsteroidDatabase.getInstance(application).asteroidDao
    }

    private val pictureDao: DailyPictureDao by lazy {
        AsteroidDatabase.getInstance(application).pictureOfDayDao
    }


    // init
    init {
        viewModelScope.launch {
            val pod = getPicture()
            _picture.value = pod;
            Log.i("Picture Of Day", pod.toString());
            // Get Asteroids
            _asteroidsList.value = getAsteroids();
        }
    }

    // GetPicture
    private suspend fun getPicture(): PictureOfDay? = withContext(Dispatchers.IO) {
        try {
            val picture = AsteroidApi.retrofitService.getPictureOfDay()
            pictureDao.insert(picture)
            pictureDao.getPictureOfDay(picture.url)
        } catch (e: Exception) {
            e.printStackTrace()
            pictureDao.getAllPictures().first()
        }
    }

    // Get All Asteroids List and Save To DB
    private suspend fun getAsteroids(): List<Asteroid> = withContext(Dispatchers.IO) {
        try {

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
            asteroidDao.getAllAsteroids()
        } catch (e: Exception) {
            e.printStackTrace()
            asteroidDao.getAllAsteroids()
        }
    }

    // Get Week
    private suspend fun getWeekAsteroids(): List<Asteroid> = withContext(Dispatchers.IO) {
        try {
            val calendar = Calendar.getInstance()
            val currentTime = calendar.time

            calendar.add(Calendar.DAY_OF_YEAR, 7)
            val futureTime = calendar.time
            val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

            asteroidDao.getAsteroidsDate(dateFormat.format(currentTime),dateFormat.format(futureTime))
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun getTodayAsteroids(): List<Asteroid> = withContext(Dispatchers.IO) {
        try {
            // Today
            val calendar = Calendar.getInstance()
            val currentTime = calendar.time
            val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

            asteroidDao.getTodayAsteroid(dateFormat.format(currentTime))
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun getSavedAsteroids(): List<Asteroid> = withContext(Dispatchers.IO) {
        try {
            asteroidDao.getAllAsteroids()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }



    // construct ViewModel
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }




    // Menu Changed
    fun onMenuItemChanged(item: AsteroidFilterOptions) {
        viewModelScope.launch {
            when (item) {
                AsteroidFilterOptions.WEEK -> {
                    _asteroidsList.value = getWeekAsteroids()
                }
                AsteroidFilterOptions.TODAY -> {
                    _asteroidsList.value = getTodayAsteroids()
                }
                else -> { // ALL Saved
                    _asteroidsList.value = getSavedAsteroids()
                }
            }
        }
    }
}
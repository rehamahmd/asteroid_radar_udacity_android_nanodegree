package com.udacity.asteroidradar.api

import com.google.gson.JsonObject
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(Constants.BASE_URL).build();

interface AsteroidService {
        @GET("neo/rest/v1/feed/")
        suspend fun getAsteroids(
            @Query("start_date") startDate: String,
            @Query("end_date") endDate: String,
            @Query("api_key") apiKey: String = Constants.API_KEY
        ):JsonObject

        @GET("planetary/apod/")
        suspend fun getPictureOfDay(
            @Query("api_key") apiKey: String = Constants.API_KEY
        ): PictureOfDay

}
object AsteroidApi {
    val retrofitService : AsteroidService by lazy {
        retrofit.create(AsteroidService::class.java)
    }
}
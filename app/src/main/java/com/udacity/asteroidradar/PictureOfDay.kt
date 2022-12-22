package com.udacity.asteroidradar

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize


//PictureOfDay(mediaType=null, title=Sun Halo at Sixty-three Degrees North, url=https://apod.nasa.gov/apod/image/2212/GS_20221217_Solhalo_Pan_v3_1100.jpg)
@Entity(tableName = "picture_table")
@Parcelize
data class PictureOfDay(
    @ColumnInfo(name = "media_type") @Json(name = "media_type") val mediaType: String? = "",
    @ColumnInfo(name = "title") val title: String? = "",
    @PrimaryKey val url: String
) : Parcelable


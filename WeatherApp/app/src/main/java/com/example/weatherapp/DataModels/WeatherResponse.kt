package com.example.weatherapp.DataModels

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


data class WeatherResponse(
                           val description: String,
                           val icon: String,
                           val id: Int,
                           val main: String
)
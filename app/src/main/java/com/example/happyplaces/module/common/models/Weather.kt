package com.example.happyplaces.module.common.models

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)
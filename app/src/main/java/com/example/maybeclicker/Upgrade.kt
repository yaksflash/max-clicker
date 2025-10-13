package com.example.maybeclicker

data class Upgrade (
    val id: String,
    val name: String,
    val description: String,
    val cost: Int,
    val type: String,
    val value: Double
)
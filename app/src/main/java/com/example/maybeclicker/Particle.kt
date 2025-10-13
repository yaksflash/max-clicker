package com.example.maybeclicker

data class Particle(
    var x: Float,
    var y: Float,
    val radius: Float,
    val color: Int,
    var alpha: Int,
    var velocityX: Float,
    var velocityY: Float
)

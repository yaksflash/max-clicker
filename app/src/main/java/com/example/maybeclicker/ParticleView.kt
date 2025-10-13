package com.example.maybeclicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class ParticleView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val particles = mutableListOf<Particle>()
    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }

    fun spawnParticles(x: Float, y: Float, count: Int = 20) {
        for (i in 0 until count) {
            particles.add(
                Particle(
                    x = x,
                    y = y,
                    radius = Random.nextFloat() * 10 + 5,
                    color = 0xFFFFFF00.toInt(), // жёлтый
                    alpha = 255,
                    velocityX = (Random.nextFloat() - 0.5f) * 10,
                    velocityY = (Random.nextFloat() - 0.5f) * 10
                )
            )
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            paint.color = p.color
            paint.alpha = p.alpha
            canvas.drawCircle(p.x, p.y, p.radius, paint)

            // Обновляем позицию
            p.x += p.velocityX
            p.y += p.velocityY
            p.alpha -= 10
            if (p.alpha <= 0) iterator.remove()
        }
        if (particles.isNotEmpty()) {
            postInvalidateOnAnimation() // продолжить анимацию
        }
    }
}

package com.example.healthyroutine

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan
class CustomDotSpan(private val radius: Float, private val color: Int) : ReplacementSpan() {
    override fun getSize(
        paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?
    ): Int {
        return (2 * radius).toInt()
    }

    override fun draw(
        canvas: Canvas, text: CharSequence?, start: Int, end: Int,
        x: Float, top: Int, y: Int, bottom: Int, paint: Paint
    ) {
        val oldColor = paint.color
        paint.color = color
        canvas.drawCircle(x + radius, (top + bottom) / 2f, radius, paint)
        paint.color = oldColor
    }
}
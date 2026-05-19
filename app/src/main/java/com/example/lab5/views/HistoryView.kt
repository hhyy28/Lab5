package com.example.lab5.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class HistoryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val history = ArrayDeque<Float>()
    private val maxItems = 50

    private val bgPaint = Paint().apply {
        color = Color.parseColor("#2a2a2a")
    }

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint().apply {
        color = Color.parseColor("#444444")
        strokeWidth = 1f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#888888")
        textSize = 22f
    }

    fun addValue(value: Float) {
        history.addLast(value)
        if (history.size > maxItems) history.removeFirst()
        invalidate()
    }

    fun clear() {
        history.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        canvas.drawLine(0f, height * 0.5f, width.toFloat(), height * 0.5f, gridPaint)
        canvas.drawText("1G", 8f, height * 0.5f - 4f, textPaint)

        if (history.isEmpty()) return

        val barWidth = width.toFloat() / maxItems
        val maxG = 2.0f

        history.forEachIndexed { i, value ->
            val barHeight = (value / maxG * height).coerceAtMost(height.toFloat())
            barPaint.color = when {
                value < 0.5f -> Color.parseColor("#4CAF50")
                value < 1.0f -> Color.parseColor("#FFC107")
                else -> Color.parseColor("#F44336")
            }
            canvas.drawRect(
                i * barWidth + 1f,
                height - barHeight,
                (i + 1) * barWidth - 1f,
                height.toFloat(),
                barPaint
            )
        }
    }
}
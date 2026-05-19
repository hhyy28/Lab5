package com.example.lab5.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class GForceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var gx: Float = 0f
    private var gy: Float = 0f
    private var magnitude: Float = 0f

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2a2a2a")
        style = Paint.Style.FILL
    }

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#555555")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val crossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#444444")
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#888888")
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#666666")
        textSize = 22f
        textAlign = Paint.Align.CENTER
    }

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val trailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    fun updateForce(forceX: Float, forceY: Float, mag: Float) {
        gx = forceX
        gy = forceY
        magnitude = mag
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val maxRadius = min(cx, cy) - 30f
        val scale = maxRadius / 2.0f

        canvas.drawCircle(cx, cy, maxRadius, bgPaint)

        canvas.drawCircle(cx, cy, maxRadius * 0.25f, circlePaint)
        canvas.drawCircle(cx, cy, maxRadius * 0.5f, circlePaint)
        canvas.drawCircle(cx, cy, maxRadius * 0.75f, circlePaint)
        canvas.drawCircle(cx, cy, maxRadius, circlePaint)

        canvas.drawLine(cx - maxRadius, cy, cx + maxRadius, cy, crossPaint)
        canvas.drawLine(cx, cy - maxRadius, cx, cy + maxRadius, crossPaint)

        canvas.drawText("0.5", cx + maxRadius * 0.25f + 20f, cy - 8f, labelPaint)
        canvas.drawText("1.0", cx + maxRadius * 0.5f + 20f, cy - 8f, labelPaint)
        canvas.drawText("1.5", cx + maxRadius * 0.75f + 20f, cy - 8f, labelPaint)
        canvas.drawText("2.0G", cx + maxRadius - 4f, cy - 8f, labelPaint)

        canvas.drawText("ВПЕРЕД", cx, cy - maxRadius - 10f, textPaint)
        canvas.drawText("НАЗАД", cx, cy + maxRadius + 30f, textPaint)
        canvas.drawText("◀", cx - maxRadius - 18f, cy + 10f, textPaint)
        canvas.drawText("▶", cx + maxRadius + 18f, cy + 10f, textPaint)

        val px = cx + (gx * scale).coerceIn(-maxRadius, maxRadius)
        val py = cy - (gy * scale).coerceIn(-maxRadius, maxRadius)

        val color = when {
            magnitude < 0.5f -> Color.parseColor("#4CAF50")
            magnitude < 1.0f -> Color.parseColor("#FFC107")
            else -> Color.parseColor("#F44336")
        }

        trailPaint.color = color and 0x40FFFFFF.toInt()
        canvas.drawCircle(px, py, 36f, trailPaint)

        pointPaint.color = color
        canvas.drawCircle(px, py, 18f, pointPaint)
    }
}
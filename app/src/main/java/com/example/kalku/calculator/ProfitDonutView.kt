package com.example.kalku.calculator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.kalku.R

/**
 * Grafik donat sederhana tanpa library tambahan.
 */
class ProfitDonutView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        color = ContextCompat.getColor(context, R.color.chart_track)
    }

    private val profitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        color = ContextCompat.getColor(context, R.color.green_profit)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = ContextCompat.getColor(context, R.color.text_dark)
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = ContextCompat.getColor(context, R.color.text_gray_light)
    }

    private var percentage: Float = 0f

    fun setPercentage(value: Double) {
        percentage = value.coerceIn(0.0, 100.0).toFloat()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val strokeWidth = width.coerceAtMost(height) * 0.12f
        trackPaint.strokeWidth = strokeWidth
        profitPaint.strokeWidth = strokeWidth

        val padding = strokeWidth / 2f + 8f
        val rect = RectF(padding, padding, width - padding, height - padding)

        canvas.drawArc(rect, -90f, 360f, false, trackPaint)
        canvas.drawArc(rect, -90f, percentage * 3.6f, false, profitPaint)

        textPaint.textSize = width.coerceAtMost(height) * 0.18f
        labelPaint.textSize = width.coerceAtMost(height) * 0.08f

        val centerX = width / 2f
        val centerY = height / 2f
        canvas.drawText("${percentage.toInt()}%", centerX, centerY + textPaint.textSize * 0.2f, textPaint)
        canvas.drawText("PROFIT", centerX, centerY + textPaint.textSize * 0.75f, labelPaint)
    }
}

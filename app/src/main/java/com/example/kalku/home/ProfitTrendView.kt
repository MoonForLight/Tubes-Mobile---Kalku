package com.example.kalku.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.kalku.R
import kotlin.math.max

class ProfitTrendView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val values = MutableList(7) { 0L }
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.orange_light)
    }
    private val activePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.orange_primary)
    }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.divider)
        strokeWidth = 1f
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.text_gray_light)
        textAlign = Paint.Align.CENTER
    }

    fun setValues(newValues: List<Long>) {
        values.clear()
        values.addAll(newValues.takeLast(7))
        while (values.size < 7) values.add(0, 0L)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val top = paddingTop.toFloat() + 8f
        val bottom = height - paddingBottom.toFloat() - 28f
        val left = paddingLeft.toFloat() + 8f
        val right = width - paddingRight.toFloat() - 8f
        val graphHeight = (bottom - top).coerceAtLeast(1f)
        val graphWidth = (right - left).coerceAtLeast(1f)

        repeat(3) { index ->
            val y = top + graphHeight * index / 2f
            canvas.drawLine(left, y, right, y, gridPaint)
        }

        val maxValue = max(values.maxOrNull() ?: 0L, 1L)
        val slot = graphWidth / 7f
        val barWidth = slot * 0.55f
        val labels = listOf("H-6", "H-5", "H-4", "H-3", "H-2", "Kem.", "Hari ini")
        labelPaint.textSize = 9f * resources.displayMetrics.scaledDensity

        values.forEachIndexed { index, value ->
            val barHeight = graphHeight * value.toFloat() / maxValue.toFloat()
            val cx = left + slot * index + slot / 2f
            val rect = RectF(cx - barWidth / 2f, bottom - barHeight, cx + barWidth / 2f, bottom)
            canvas.drawRoundRect(rect, 8f, 8f, if (index == 6) activePaint else barPaint)
            canvas.drawText(labels[index], cx, height - paddingBottom.toFloat() - 6f, labelPaint)
        }
    }
}

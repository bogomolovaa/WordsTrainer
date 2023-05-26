package bogomolov.aa.wordstrainer.features.repetition

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.dp


private const val DESIRED_HEIGHT = 100
private const val DESIRED_WIDTH = 200

class WordView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> DESIRED_WIDTH.coerceAtMost(widthSize)
            else -> DESIRED_WIDTH
        }
        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> DESIRED_HEIGHT.coerceAtMost(heightSize)
            else -> DESIRED_HEIGHT
        }
        setMeasuredDimension(width, height)
    }

    var text: String = ""
        set(value) {
            field = value
            invalidate()
        }

    private val strokePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f.dp.toFloat()
        color = ContextCompat.getColor(context, R.color.card_outline)
    }

    private val fillPaint = Paint().apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.card_fill)
    }

    private val textPaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        color = Color.BLACK
        textSize = 24f.dp.toFloat()
    }

    private val path = Path()

    private val radius = 16f.dp

    private fun resetPath(rect: RectF) {
        path.reset()
        path.addRoundRect(rect, radius.toFloat(), radius.toFloat(), Path.Direction.CW)
        path.close()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        resetPath(RectF(0f, 0f, w.toFloat(), h.toFloat()))
    }

    override fun draw(canvas: Canvas) {
        canvas.clipPath(path)
        super.draw(canvas)

        canvas.apply {
            drawRoundRect(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                fillPaint
            )
            drawRoundRect(
                strokePaint.strokeWidth / 2,
                strokePaint.strokeWidth / 2,
                width.toFloat() - strokePaint.strokeWidth / 2,
                height.toFloat() - strokePaint.strokeWidth / 2,
                radius.toFloat(),
                radius.toFloat(),
                strokePaint
            )
            val textX = width / 2
            val textY = height / 2 - (textPaint.descent() + textPaint.ascent()) / 2
            drawText(text, textX.toFloat(), textY, textPaint)
        }
    }
}
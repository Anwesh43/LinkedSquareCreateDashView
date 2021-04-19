package com.example.squarecreatedashview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

val colors : Array<Int> = arrayOf(
    "#f44336",
    "#673AB7",
    "#4CAF50",
    "#00C853",
    "#1A237E"
).map {
    Color.parseColor(it)
}.toTypedArray()
val scGap : Float = 0.02f
val delay : Long = 20
val strokeFactor : Float = 90f
val sizeFactor : Float = 3.9f
val barFactor : Float = 6.6f
val rot : Float = 90f
val backColor : Int = Color.parseColor("#BDBDBD")
val lines : Int = 4
val parts : Int = 1 + lines

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawSquareCreateDash(scale : Float, w : Float, h : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val size : Float = Math.min(w, h) / sizeFactor
    val barSize : Float = Math.min(w, h) / barFactor
    save()
    translate(w / 2, h / 2)
    for (j in 0..(lines - 1)) {
        save()
        rotate(rot * j)
        drawLine(
            size,
            -size,
            size,
            -size + 2 * size * sf.divideScale(j, parts),
            paint
        )
        restore()
    }
    for (j in 0..1) {
        save()
        scale(1f - 2 * j, 1f)
        drawRect(
            RectF(
                -size,
                -size,
                -size + barSize * sf.divideScale(lines, parts),
                size
            ),
            paint
        )
        restore()
    }
    restore()
}

fun Canvas.drawSCDNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawSquareCreateDash(scale, w, h, paint)
}

class SquareCreateDashView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}
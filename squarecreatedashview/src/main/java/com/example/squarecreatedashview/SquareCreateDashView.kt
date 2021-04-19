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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SCDNode(var i : Int, val state : State = State()) {

        private var next : SCDNode? = null
        private var prev : SCDNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = SCDNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSCDNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SCDNode {
            var curr : SCDNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class SquareCreateDash(var i : Int) {

        private var curr : SCDNode = SCDNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : SquareCreateDashView) {

        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val scd : SquareCreateDash = SquareCreateDash(0)
        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            scd.draw(canvas, paint)
            animator.animate {
                scd.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            scd.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : SquareCreateDashView {
            val view : SquareCreateDashView = SquareCreateDashView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
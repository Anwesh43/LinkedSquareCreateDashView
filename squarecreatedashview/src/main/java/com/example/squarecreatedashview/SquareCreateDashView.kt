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
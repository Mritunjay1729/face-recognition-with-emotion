package com.example.facedetectionusingcamera.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import com.example.facedetectionusingcamera.ui_components.GraphicsOverlay

class CameraImageGraphics(overlay: GraphicsOverlay?, private val bitmap: Bitmap) : GraphicsOverlay.Graphic(overlay!!) {
    override fun draw(canvas: Canvas?) {
        canvas!!.drawBitmap(bitmap, null, Rect(0, 0, canvas.width, canvas.height), null)
    }
}
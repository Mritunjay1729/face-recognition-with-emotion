package com.example.facedetectionusingcamera.visions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.facedetectionusingcamera.interfaces.FaceDetectStatus
import com.example.facedetectionusingcamera.models.RectModel
import com.example.facedetectionusingcamera.ui_components.GraphicsOverlay
import com.google.firebase.ml.vision.face.FirebaseVisionFace

class FaceGraphics internal constructor(
    overlay: GraphicsOverlay?,
    private val firebaseVisionFace: FirebaseVisionFace,
    private val facing: Int,
    private val overlayBitmap: Bitmap
    ) : GraphicsOverlay.Graphic(overlay!!){
        private val facePositionPaint: Paint
        private val idPaint: Paint
        private val boxPaint: Paint

        var faceDetectStatus: FaceDetectStatus? = null

        override  fun draw(canvas: Canvas?) {
            val face: FirebaseVisionFace = firebaseVisionFace ?: return

            val x: Float = translateX(face.boundingBox.centerX().toFloat())
            val y: Float = translateY(face.boundingBox.centerY().toFloat())

            //Draw a bounding box around the face.
            val xOffset: Float = scaleX(face.boundingBox.width() / 2.0f)
            val yOffset: Float = scaleY(face.boundingBox.height() / 2.0f)
            val left: Float = x - xOffset
            val top: Float = y - yOffset
            val right: Float = x + xOffset
            val bottom: Float = y + yOffset
            canvas!!.drawRect(left, top, right, bottom, boxPaint)
            if(left < 190 && top < 450 && right > 850 && bottom > 1050) {
                if(faceDetectStatus != null) faceDetectStatus!!.onFaceLocated(RectModel(left, top, right, bottom))
            }else {
                if(faceDetectStatus != null) faceDetectStatus!!.onFaceNotLocated()
            }
        }

    companion object {
        private const val ID_TEXT_SIZE = 30.0f
        private const val BOX_STROKE_WIDTH = 5.0f
    }

    init {
        val selectedColor: Int = Color.RED
        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor
        idPaint = Paint()
        idPaint.color = selectedColor
        idPaint.textSize = ID_TEXT_SIZE
        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH
    }
}
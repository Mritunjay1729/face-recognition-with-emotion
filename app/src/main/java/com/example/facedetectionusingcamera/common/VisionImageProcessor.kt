package com.example.facedetectionusingcamera.common

import android.graphics.Bitmap
import com.example.facedetectionusingcamera.ui_components.GraphicsOverlay
import com.google.firebase.FirebaseException
import java.nio.ByteBuffer

interface VisionImageProcessor {
    /** Processes the images with the underlying machine learning mode */
    /** ByteBuffer is essentially the set of images available to work upon */
    @Throws(FirebaseException::class)
    fun process(data: ByteBuffer?, frameMetaData: FrameMetaData?, graphicsOverlay: GraphicsOverlay)

    fun process(bitmap: Bitmap?, graphicsOverlay: GraphicsOverlay?)

    /** Stop the underlying machine learning model and release resources */
    fun stop()
}
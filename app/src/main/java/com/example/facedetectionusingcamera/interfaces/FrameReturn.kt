package com.example.facedetectionusingcamera.interfaces

import android.graphics.Bitmap
import com.example.facedetectionusingcamera.common.FrameMetaData
import com.example.facedetectionusingcamera.ui_components.GraphicsOverlay
import com.google.firebase.ml.vision.face.FirebaseVisionFace

//For case, when frame is detected
interface FrameReturn {
    fun onFrame(
        image: Bitmap?,
        face: FirebaseVisionFace?,
        frameMetaData: FrameMetaData?,
        graphicsOverlay: GraphicsOverlay?
    )
}
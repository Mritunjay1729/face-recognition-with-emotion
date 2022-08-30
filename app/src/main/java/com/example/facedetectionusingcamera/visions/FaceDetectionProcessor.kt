package com.example.facedetectionusingcamera.visions

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.facedetectionusingcamera.R
import com.example.facedetectionusingcamera.common.CameraImageGraphics
import com.example.facedetectionusingcamera.common.FrameMetaData
import com.example.facedetectionusingcamera.common.VisionProcessorBase
import com.example.facedetectionusingcamera.interfaces.FaceDetectStatus
import com.example.facedetectionusingcamera.interfaces.FrameReturn
import com.example.facedetectionusingcamera.models.RectModel
import com.example.facedetectionusingcamera.ui_components.GraphicsOverlay
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import java.io.IOException
import java.lang.Exception

class FaceDetectionProcessor(resources: Resources?) : VisionProcessorBase<List<FirebaseVisionFace?>?>(), FaceDetectStatus {
    var faceDetectStatus: FaceDetectStatus? = null
    private val detector: FirebaseVisionFaceDetector
    private val overlayBitmap: Bitmap
    var frameHandler: FrameReturn? = null

    override fun stop() {
        try{
            detector.close()
        } catch (e : IOException) {
            Log.e(TAG, "Exception thrown while trying to close face detection: $e")
        }
    }

    override fun detectInImage(image: FirebaseVisionImage?): Task<List<FirebaseVisionFace?>?> {
        return detector.detectInImage(image!!)
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Face detection Failed $e")
    }

    override fun onFaceLocated(rectModel: RectModel?) {
        if(faceDetectStatus != null) faceDetectStatus!!.onFaceLocated(rectModel)
    }

    override fun onFaceNotLocated() {
        if(faceDetectStatus != null) faceDetectStatus!!.onFaceNotLocated()
    }

    companion object{
        private const val TAG = "FaceDetectionProcessor"
    }

    init{
        val options: FirebaseVisionFaceDetectorOptions = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()
        detector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        overlayBitmap = BitmapFactory.decodeResource(resources, R.drawable.clown_nose)
    }

    override fun onSuccess(
        originalCameraImage: Bitmap?,
        faces: List<FirebaseVisionFace?>?,
        frameMetaData: FrameMetaData,
        graphicsOverlay: GraphicsOverlay
    ) {
        graphicsOverlay.clear()
        if(originalCameraImage != null) {
            val imageGraphics = CameraImageGraphics(graphicsOverlay, originalCameraImage)
            graphicsOverlay.add(imageGraphics)
        }
        for(i in faces!!.indices) {
            val face: FirebaseVisionFace? = faces[i]
            if(frameHandler != null) {
                frameHandler!!.onFrame(originalCameraImage, face, frameMetaData, graphicsOverlay)
            }
            val cameraFacing: Int = frameMetaData?.cameraFacing ?: android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK
            val faceGraphics = FaceGraphics(graphicsOverlay, face!!, cameraFacing, overlayBitmap)
            faceGraphics.faceDetectStatus = this
            graphicsOverlay.add(faceGraphics)
        }
        graphicsOverlay.postInvalidate()
    }
}
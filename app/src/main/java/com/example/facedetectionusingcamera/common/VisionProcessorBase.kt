package com.example.facedetectionusingcamera.common

import android.graphics.Bitmap
import androidx.annotation.GuardedBy
import com.example.facedetectionusingcamera.common.BitmapUtils.getBitmap
import com.example.facedetectionusingcamera.ui_components.GraphicsOverlay
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.lang.Exception
import java.nio.ByteBuffer


//Holds the image as well as the meta-data associated with that image
abstract class VisionProcessorBase<T> : VisionImageProcessor{
    // keep the data of the latest images and its metadata
    @GuardedBy("this")
    private var latestImage: ByteBuffer? = null

    @GuardedBy("this")
    private var latestImageMetaData: FrameMetaData? = null

    //keeps the data of the latest images and its metadata which are currently under process
    @GuardedBy("this")
    private var processingImage: ByteBuffer? = null

    @GuardedBy("this")
    private var processingImageMetadata : FrameMetaData? = null

    override fun process(
        data: ByteBuffer?,
        frameMetaData: FrameMetaData?,
        graphicsOverlay: GraphicsOverlay
    ) {
        latestImage = data
        latestImageMetaData = frameMetaData
        if(processingImage == null && processingImageMetadata == null) {
            processLatestImage(graphicsOverlay)
        }
    }

    override fun process(bitmap: Bitmap?, graphicsOverlay: GraphicsOverlay?) {
        detectInVisionImage(null, FirebaseVisionImage.fromBitmap(bitmap!!), null,
        graphicsOverlay!!)
    }

    @Synchronized
    private fun processLatestImage(graphicsOverlay: GraphicsOverlay) {
        processingImage = latestImage
        processingImageMetadata = latestImageMetaData
        latestImage = null
        latestImageMetaData = null
        if(processingImage != null && processingImageMetadata != null) {
            processImage(processingImage!!, processingImageMetadata!!, graphicsOverlay)
        }
    }

    private fun processImage(
        data: ByteBuffer, frameMetaData: FrameMetaData,
        graphicsOverlay: GraphicsOverlay) {
        val metaData: FirebaseVisionImageMetadata = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setWidth(frameMetaData.width)
            .setHeight(frameMetaData.height)
            .setRotation(frameMetaData.rotation)
            .build()
        val bitmap: Bitmap? = getBitmap(data, frameMetaData)
        detectInVisionImage(
            bitmap, FirebaseVisionImage.fromByteBuffer(data, metaData), frameMetaData,
            graphicsOverlay
        )
    }

    private fun detectInVisionImage(
        originalCameraImage: Bitmap?,
        image: FirebaseVisionImage,
        metadata: FrameMetaData?,
        graphicsOverlay: GraphicsOverlay) {
        detectInImage(image)
            .addOnSuccessListener { results ->
                this@VisionProcessorBase.onSuccess(originalCameraImage, results,
                metadata!!,
                graphicsOverlay)
                processLatestImage(graphicsOverlay)
            }
            .addOnFailureListener { e -> this@VisionProcessorBase.onFailure(e)}
    }

    override  fun stop(){}
    protected abstract fun detectInImage(image: FirebaseVisionImage?): Task<T>

    protected abstract fun onSuccess(
        originalCameraImage: Bitmap?,
        result: T,
        frameMetaData: FrameMetaData,
        graphicsOverlay: GraphicsOverlay
    )

    protected abstract fun onFailure(e : Exception)
}
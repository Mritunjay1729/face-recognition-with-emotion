package com.example.facedetectionusingcamera

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.facedetectionusingcamera.R
import com.example.facedetectionusingcamera.base.BaseActivity
import com.example.facedetectionusingcamera.base.PublicMethods
import com.example.facedetectionusingcamera.common.CameraSource
import com.example.facedetectionusingcamera.common.FrameMetaData
import com.example.facedetectionusingcamera.interfaces.FaceDetectStatus
import com.example.facedetectionusingcamera.interfaces.FrameReturn
import com.example.facedetectionusingcamera.models.RectModel
import com.example.facedetectionusingcamera.ui_components.CameraPreview
import com.example.facedetectionusingcamera.ui_components.GraphicsOverlay
import com.example.facedetectionusingcamera.visions.FaceDetectionProcessor
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.hsalf.smilerating.SmileRating
import java.io.IOException
import java.lang.Exception

class MainActivity : BaseActivity(), ActivityCompat.OnRequestPermissionsResultCallback, FrameReturn, FaceDetectStatus {
    var originalImage: Bitmap? = null
    private var cameraSource: CameraSource? = null
    private var preview: CameraPreview? = null
    private var graphicsOverlay: GraphicsOverlay? = null
    private var faceFrame: ImageView? = null
    private var test: ImageView? = null
    private var takePhoto: Button? = null
    private var smile_rating: SmileRating? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        test = findViewById(R.id.test)
        preview = findViewById(R.id.preview)
        takePhoto = findViewById(R.id.takePhoto)
        graphicsOverlay = findViewById(R.id.overlay)
        smile_rating = findViewById(R.id.smile_rating)

        if(PublicMethods.allPermissionsGranted(this)) {
            createCameraSource()
        }else{
            PublicMethods.getRuntimeRuntimePermission(this)
        }
    }

    private fun createCameraSource() {
        if (cameraSource == null) {
            cameraSource = CameraSource(this, graphicsOverlay!!)
        }

        try {
            val processor = FaceDetectionProcessor(resources)
            processor.frameHandler = this
            processor.faceDetectStatus = this
            cameraSource!!.setMachineLearningFrameProcessor(processor)
        } catch (e: Exception) {
            Log.e(TAG, "Can not create image processor: $FACE_DETECTION", e)
            Toast.makeText(
                applicationContext,
                "Can not create image processor: " + e.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    public override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        preview!!.stop()
    }

    companion object {
        private  const val FACE_DETECTION = "Face Detection"
        private  const val TAG = "MLKitTAG"
    }

    private fun startCameraSource() {
        if(cameraSource != null) {
            try {
                preview!!.start(cameraSource, graphicsOverlay)
            }catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource!!.release()
                cameraSource = null
            }
        }
    }

    override fun onFrame(
        image: Bitmap?,
        face: FirebaseVisionFace?,
        frameMetaData: FrameMetaData?,
        graphicsOverlay: GraphicsOverlay?
    ) {
        TODO("Not yet implemented")
    }

    override fun onFaceNotLocated() {
        TODO("Not yet implemented")
    }

    override fun onFaceLocated(rectModel: RectModel?) {
        TODO("Not yet implemented")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(PublicMethods.allPermissionsGranted(this)) {
            createCameraSource()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
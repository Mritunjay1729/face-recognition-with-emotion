package com.example.facedetectionusingcamera.ui_components

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import com.example.facedetectionusingcamera.common.CameraSource
import java.io.IOException

class CameraPreview  (context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs){
    private val surfaceView: SurfaceView
    private var startRequested = false
    private var surfaceAvailable = false
    private var cameraSource: CameraSource? = null
    private var overlay: GraphicsOverlay? = null

    companion object {
        private const val TAG = "MIDemoApp: Preview"
    }

    init {
        surfaceView = SurfaceView(context)
        surfaceView.holder.addCallback(SurfaceCallback())
        addView(surfaceView)
    }

    @Throws(IOException::class)
    fun start(cameraSource: CameraSource?) {
        if(cameraSource == null) stop()
        this.cameraSource = cameraSource
        if(this.cameraSource != null) {
            startRequested = true
            startIfReady()
        }
    }

    @Throws(IOException::class)
    fun start(cameraSource: CameraSource?, overlay: GraphicsOverlay?) {
        this.overlay = overlay
        start(cameraSource)
    }

    fun stop() {
        if(cameraSource != null) {
            cameraSource!!.stop()
        }
    }

    fun release() {
        if(cameraSource != null) {
            cameraSource!!.release()
            cameraSource = null
        }
    }

    @SuppressLint("MissingPermission")
    @Throws(IOException::class)
    private fun startIfReady() {
        if(startRequested && surfaceAvailable) {
            cameraSource!!.start()
            if(overlay != null) {
                val size = cameraSource!!.previewSize
                val min = Math.min(size!!.width, size.height)
                val max = Math.max(size.width, size.height)
                if(isPortraitMode) {
                    overlay!!.setCameraInfo(min, max, cameraSource!!.cameraFacing)
                }else{
                    overlay!!.setCameraInfo(max, min, cameraSource!!.cameraFacing)
                }
                overlay!!.clear()
            }
            startRequested = false
        }
    }

    private val isPortraitMode: Boolean
        get() {
            val orientation = context.resources.configuration.orientation
            if(orientation == Configuration.ORIENTATION_PORTRAIT) return true
            if(orientation == Configuration.ORIENTATION_LANDSCAPE) return false
            Log.d(TAG, "isPortraitMode returning false by default")
            return false
        }


    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        var width = 320
        var height = 240
        if(cameraSource != null) {
            val size = cameraSource!!.previewSize
            if(size != null) {
                width = size.width
                height = size.height
            }
        }

        if(isPortraitMode) {
            val tmp = width
            width = height
            height = tmp
        }
        val layoutWidth = right - left
        val layoutHeight = bottom - top

        var childWidth = layoutWidth
        var childHeight =(layoutWidth.toFloat()/ width.toFloat() * height).toInt()

        if(childHeight > layoutHeight) {
            childHeight = layoutHeight
            childWidth = (layoutHeight.toFloat()/ height.toFloat() * width).toInt()
        }

        for(i in 0 until childCount) {
            getChildAt(i).layout(0, 0, childWidth, childHeight)
            Log.d(TAG, "Assigned view: $i")
        }

        try{
            startIfReady()
        } catch (e: IOException) {
            Log.e(TAG, "Could not start camera source", e)
        }
    }

    //Surface holding our camera
    private inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surface: SurfaceHolder) {
            surfaceAvailable = true
            try {
                startIfReady()
            } catch (e : IOException) {
                Log.e(TAG, "Could not start camera source", e)
            }
        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            surfaceAvailable = false
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int){}
    }
}
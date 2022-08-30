package com.example.facedetectionusingcamera.interfaces

import com.example.facedetectionusingcamera.models.RectModel

interface FaceDetectStatus {
    fun onFaceLocated(rectModel: RectModel?)
    fun onFaceNotLocated()
}
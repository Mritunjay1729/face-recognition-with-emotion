package com.example.facedetectionusingcamera.base

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.Exception

object PublicMethods {
    private const val PERMISSION_REQUESTS = 1
    private fun getRequiredPermission(mActivity: Activity): Array<String?> {
        return try {
            val info: PackageInfo = mActivity.packageManager
                .getPackageInfo(mActivity.packageName, PackageManager.GET_PERMISSIONS)
            val ps = info.requestedPermissions
            if(ps != null && ps.isNotEmpty()) {
                return ps
            } else {
                arrayOfNulls(0)
            }
        }catch (e : Exception) {
            arrayOfNulls(0)
        }
    }

    fun allPermissionsGranted(mActivity: Activity) : Boolean {
        for(permission in getRequiredPermission(mActivity)) {
            if(!isPermissionGranted(mActivity, permission)) {
                return false
            }
        }
        return  true
    }

    fun getRuntimeRuntimePermission(mActivity: Activity) {
        val allNeededPermissions: MutableList<String?> = ArrayList()
        for(permission in getRequiredPermission(mActivity)) {
            if(!isPermissionGranted(mActivity, permission)) {
                allNeededPermissions.add(permission)
            }
        }
        if(!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                mActivity, allNeededPermissions.toTypedArray(), PERMISSION_REQUESTS
            )
        }
    }

    private fun isPermissionGranted(context: Context, permission: String?): Boolean {
        return (ContextCompat.checkSelfPermission(context, permission!!)
        == PackageManager.PERMISSION_GRANTED)
    }
}
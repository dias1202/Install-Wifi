package com.dias.installwifi.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

object PermissionHelper {

    private const val TAG = "PermissionHelper"

    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "Using READ_MEDIA_IMAGES for Android 13+")
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            Log.d(TAG, "Using READ_EXTERNAL_STORAGE for Android <13")
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    fun hasPermissions(context: Context): Boolean {
        val permissions = getRequiredPermissions()
        val results = permissions.map {
            val granted = ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            Log.d(TAG, "Permission $it granted: $granted")
            granted
        }
        return results.all { it }
    }

    fun requestPermissionsIfNeeded(
        context: Context,
        permissionLauncher: ActivityResultLauncher<Array<String>>
    ) {
        val permissions = getRequiredPermissions()
        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGranted.isNotEmpty()) {
            Log.d(TAG, "Requesting permissions: $notGranted")
            permissionLauncher.launch(permissions)
        } else {
            Log.d(TAG, "All permissions already granted")
        }
    }
}

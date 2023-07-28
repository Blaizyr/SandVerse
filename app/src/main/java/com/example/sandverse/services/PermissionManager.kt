package com.example.sandverse.services

import android.content.Context
import android.content.pm.PackageManager
import android.Manifest
import android.util.Log.d
import android.util.Log.e

class PermissionManager {
    fun checkPermission(context: Context, permission: Permission): Boolean {
        val permissionString = permission.permissionString
        val permissionName = permission.permissionName
        return if (context.checkSelfPermission(permissionString) == PackageManager.PERMISSION_GRANTED) {
            d("PermissionManager", "$permissionName permission granted")
            true
        } else {
            e("PermissionManager", "$permissionName permission not granted")
            throw Exception("Set permissions in smartphone settings!")
        }
    }
}

enum class Permission(val permissionString: String, val permissionName: String) {
    ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION, "Coarse Location"),
    ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION, "Fine Location")
}